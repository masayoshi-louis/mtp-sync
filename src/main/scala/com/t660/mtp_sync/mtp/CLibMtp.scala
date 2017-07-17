package com.t660.mtp_sync.mtp

import java.io.File

import com.t660.mtp_sync._
import com.t660.mtp_sync.util.PathUtils
import libmtp.LibmtpLibrary.LIBMTP_error_number_t.{LIBMTP_ERROR_NONE, LIBMTP_ERROR_NO_DEVICE_ATTACHED}
import libmtp.LibmtpLibrary.LIBMTP_filetype_t.LIBMTP_FILETYPE_FOLDER
import libmtp.LibmtpLibrary._
import libmtp._
import org.apache.commons.io.FilenameUtils
import org.bridj.{IntValuedEnum, Pointer}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by luyi on 15/07/2017.
  */
object CLibMtp {

  final val RootFileId = 0

  LIBMTP_Init()

  var err: IntValuedEnum[LibmtpLibrary.LIBMTP_error_number_t] = _

  val (rawDevices, numDevices) = {
    val rawDevicesPtr = Pointer.allocatePointer(classOf[LIBMTP_raw_device_struct])
    val numDevicesPtr = Pointer.allocateInt
    err = LIBMTP_Detect_Raw_Devices(rawDevicesPtr, numDevicesPtr)
    if (err eq LIBMTP_ERROR_NO_DEVICE_ATTACHED) {
      throw new RuntimeException("no device attached!")
    } else if (err eq LIBMTP_ERROR_NONE) { // OK
      (rawDevicesPtr.get, numDevicesPtr.get)
    } else {
      throw new RuntimeException("unexpected error " + err)
    }
  }

  def openDevice(i: Int): MtpDevice = {
    require(i < numDevices)
    val raw = if (i == 0) rawDevices else rawDevices.next(i)
    new MtpDeviceImpl(LIBMTP_Open_Raw_Device_Uncached(raw))
  }

  private class MtpDeviceImpl(devicePtr: Pointer[LIBMTP_mtpdevice_struct]) extends MtpDevice {
    require(devicePtr != null, "unable to open the device!")

    override lazy val storages: Seq[DeviceStorage] = {
      val ptr = devicePtr.get.storage

      @tailrec
      def getAll(ptr: Pointer[LIBMTP_devicestorage_struct] = ptr, result: List[DeviceStorage] = Nil): List[DeviceStorage] = {
        if (ptr == null) result else {
          val s = new DeviceStorageImpl(devicePtr, ptr)
          getAll(ptr.get.next(), s :: result)
        }
      }

      getAll().reverse
    }
  }

  private class DeviceStorageImpl(devicePtr: Pointer[LIBMTP_mtpdevice_struct],
                                  storagePtr: Pointer[LIBMTP_devicestorage_struct]) extends DeviceStorage {
    override def description: String = storagePtr.get.StorageDescription().getCString

    override def id: Int = storagePtr.get.id

    override def listFiles(fromPath: Seq[String]): MtpFile = {
      val storageId = storagePtr.get.id
      val fromPathLen = fromPath.size

      def filterPath(curPath: Seq[String]): Boolean = {
        val curPathLen = curPath.size
        val len = Math.min(curPathLen, fromPathLen)
        curPath.take(len) == fromPath.take(len)
      }

      def listFiles(path: Seq[String], id: Int): Seq[MtpFile] = {
        val filesPtr = LIBMTP_Get_Files_And_Folders(devicePtr, storageId, id)
        var filePtr = filesPtr
        val arrBuff = new ArrayBuffer[MtpFile]()

        while (filePtr != null) {
          val fileStruct = filePtr.get
          val curPath = path :+ fileStruct.filename.getCString
          if (filterPath(curPath)) {
            val isFolder = fileStruct.filetype == LIBMTP_FILETYPE_FOLDER
            val children = if (isFolder) {
              listFiles(curPath, fileStruct.item_id)
            } else {
              Seq.empty
            }

            arrBuff += MtpFile(
              id = fileStruct.item_id,
              path = curPath,
              isFolder = isFolder,
              parentId = id,
              storageId = storageId,
              size = fileStruct.filesize(),
              modificationDate = fileStruct.modificationdate,
              children = children
            )
          }
          // Next
          val tmp = filePtr
          filePtr = fileStruct.next()
          LIBMTP_destroy_file_t(tmp)
        }
        val arr = arrBuff.toArray
        arr.toSeq
      }

      MtpFile(
        id = RootFileId,
        path = Seq.empty,
        isFolder = true,
        parentId = RootFileId,
        storageId = storageId,
        size = 0,
        modificationDate = 0,
        children = listFiles(Seq.empty, LIBMTP_FILES_AND_FOLDERS_ROOT.asInstanceOf[Int])
      )
    }

    override def getFs(bindToRoot: MtpFile): FileSystem with FileCopyComponent = new FileSystem with FileCopyComponent {
      require(bindToRoot.id == RootFileId)
      val pathsMap = {
        val tmp = new mutable.HashMap[Seq[String], MtpFile]()
        for (f <- IFile.flatten(bindToRoot)) {
          tmp += f.path -> f.asInstanceOf[MtpFile]
        }
        tmp
      }

      override def remove(file: IFile): Unit = file match {
        case mtpFile: MtpFile => {
          val ret = LIBMTP_Delete_Object(devicePtr, mtpFile.id)
          if (ret != LIBMTP_ERROR_NONE.value) {
            LIBMTP_Dump_Errorstack(devicePtr)
            LIBMTP_Clear_Errorstack(devicePtr)
            throw new RuntimeException("failed to delete file " + mtpFile)
          }
        }
      }

      override def mkdir(path: Seq[String]): Unit = if (!pathsMap.contains(path)) path match {
        case parentPath :+ name => {
          val parent = find(parentPath).get
          val nameBuff = Pointer.allocateBytes((name.length + 1) * 4)
          val newId = try {
            nameBuff.setCString(name)
            LIBMTP_Create_Folder(devicePtr, nameBuff, parent.id, DeviceStorageImpl.this.id)
          } finally {
            nameBuff.release()
          }
          if (newId == 0) {
            LIBMTP_Dump_Errorstack(devicePtr)
            LIBMTP_Clear_Errorstack(devicePtr)
            throw new RuntimeException("failed to create folder " + PathUtils.stringify(path))
          } else {
            val structPtr = LIBMTP_Get_Filemetadata(devicePtr, newId)
            if (structPtr == null)
              throw new RuntimeException("failed to get new folder meta")
            try {
              val struct = structPtr.get
              val newFile = MtpFile(
                id = newId,
                path = path,
                isFolder = true,
                parentId = struct.parent_id,
                storageId = DeviceStorageImpl.this.id,
                size = 0,
                modificationDate = struct.modificationdate
              )
              pathsMap += path -> newFile
            } finally {
              LIBMTP_destroy_file_t(structPtr)
            }
          }
        }
      }

      override def copy = (src: IFile, dst: Seq[String]) => src match {
        case localSrc: LocalFile => {
          val parent = find(dst.dropRight(1)).get
          val name = dst.last
          val typeExt = FilenameUtils.getExtension(name)
          val srcPath = PathUtils.stringify(src.path)
          val filenameBuff = Pointer.allocateBytes((name.length + 1) * 4)
          val srcPathBuff = Pointer.allocateBytes((srcPath.length + 1) * 4)
          val newFilePtr = LIBMTP_new_file_t()
          val ret = try {
            filenameBuff.setCString(name)
            srcPathBuff.setCString(srcPath)
            val newFile = newFilePtr.get
            newFile.filename(filenameBuff)
            newFile.parent_id(parent.id)
            newFile.modificationdate(src.modificationDate / 1000)
            newFile.filesize(src.size)
            newFile.filetype(getFileType(typeExt))
            newFile.storage_id(DeviceStorageImpl.this.id)
            LIBMTP_Send_File_From_File(devicePtr, srcPathBuff, newFilePtr, copyCb.toPointer, null)
          } finally {
            srcPathBuff.release()
            LIBMTP_destroy_file_t(newFilePtr) // this function also releases filenameBuff
          }
          if (ret != LIBMTP_ERROR_NONE.value) {
            LIBMTP_Dump_Errorstack(devicePtr)
            LIBMTP_Clear_Errorstack(devicePtr)
            throw new RuntimeException("failed to copy file to" + PathUtils.stringify(dst))
          }
        }
        case mtpSrc: MtpFile => {
          val dstPath = PathUtils.stringify(dst)
          val dstPathBuff = Pointer.allocateBytes((dstPath.length + 1) * 4)
          val ret = try {
            dstPathBuff.setCString(dstPath)
            LIBMTP_Get_File_To_File(devicePtr, mtpSrc.id, dstPathBuff, copyCb.toPointer, null)
          } finally {
            dstPathBuff.release()
          }
          if (ret != LIBMTP_ERROR_NONE.value) {
            LIBMTP_Dump_Errorstack(devicePtr)
            LIBMTP_Clear_Errorstack(devicePtr)
            throw new RuntimeException("failed to copy file to" + dstPath)
          }
          val jFile = new File(dstPath)
          if (!jFile.setLastModified(mtpSrc.modificationDate * 1000))
            throw new RuntimeException("can not set LastModified")
        }
      }

      @inline
      private[this] def find(path: Seq[String]): Option[MtpFile] = pathsMap.get(path)

      @inline
      private[this] def strcasecmp(a: String, b: String) = {
        if (a != null) a.equalsIgnoreCase(b)
        else if (b != null) b.equalsIgnoreCase(a)
        else true
      }

      private[this] def getFileType(typeExt: String): LIBMTP_filetype_t = {
        import LIBMTP_filetype_t._
        var filetype: LIBMTP_filetype_t = null
        if (!strcasecmp(typeExt, "wav")) filetype = LIBMTP_FILETYPE_WAV
        else if (!strcasecmp(typeExt, "mp3")) filetype = LIBMTP_FILETYPE_MP3
        else if (!strcasecmp(typeExt, "wma")) filetype = LIBMTP_FILETYPE_WMA
        else if (!strcasecmp(typeExt, "ogg")) filetype = LIBMTP_FILETYPE_OGG
        else if (!strcasecmp(typeExt, "mp4")) filetype = LIBMTP_FILETYPE_MP4
        else if (!strcasecmp(typeExt, "wmv")) filetype = LIBMTP_FILETYPE_WMV
        else if (!strcasecmp(typeExt, "avi")) filetype = LIBMTP_FILETYPE_AVI
        else if (!strcasecmp(typeExt, "mpeg") || !strcasecmp(typeExt, "mpg")) filetype = LIBMTP_FILETYPE_MPEG
        else if (!strcasecmp(typeExt, "asf")) filetype = LIBMTP_FILETYPE_ASF
        else if (!strcasecmp(typeExt, "qt") || !strcasecmp(typeExt, "mov")) filetype = LIBMTP_FILETYPE_QT
        else if (!strcasecmp(typeExt, "wma")) filetype = LIBMTP_FILETYPE_WMA
        else if (!strcasecmp(typeExt, "jpg") || !strcasecmp(typeExt, "jpeg")) filetype = LIBMTP_FILETYPE_JPEG
        else if (!strcasecmp(typeExt, "jfif")) filetype = LIBMTP_FILETYPE_JFIF
        else if (!strcasecmp(typeExt, "tif") || !strcasecmp(typeExt, "tiff")) filetype = LIBMTP_FILETYPE_TIFF
        else if (!strcasecmp(typeExt, "bmp")) filetype = LIBMTP_FILETYPE_BMP
        else if (!strcasecmp(typeExt, "gif")) filetype = LIBMTP_FILETYPE_GIF
        else if (!strcasecmp(typeExt, "pic") || !strcasecmp(typeExt, "pict")) filetype = LIBMTP_FILETYPE_PICT
        else if (!strcasecmp(typeExt, "png")) filetype = LIBMTP_FILETYPE_PNG
        else if (!strcasecmp(typeExt, "wmf")) filetype = LIBMTP_FILETYPE_WINDOWSIMAGEFORMAT
        else if (!strcasecmp(typeExt, "ics")) filetype = LIBMTP_FILETYPE_VCALENDAR2
        else if (!strcasecmp(typeExt, "exe") || !strcasecmp(typeExt, "com") || !strcasecmp(typeExt, "bat") || !strcasecmp(typeExt, "dll") || !strcasecmp(typeExt, "sys")) filetype = LIBMTP_FILETYPE_WINEXEC
        else if (!strcasecmp(typeExt, "aac")) filetype = LIBMTP_FILETYPE_AAC
        else if (!strcasecmp(typeExt, "mp2")) filetype = LIBMTP_FILETYPE_MP2
        else if (!strcasecmp(typeExt, "flac")) filetype = LIBMTP_FILETYPE_FLAC
        else if (!strcasecmp(typeExt, "m4a")) filetype = LIBMTP_FILETYPE_M4A
        else if (!strcasecmp(typeExt, "doc")) filetype = LIBMTP_FILETYPE_DOC
        else if (!strcasecmp(typeExt, "xml")) filetype = LIBMTP_FILETYPE_XML
        else if (!strcasecmp(typeExt, "xls")) filetype = LIBMTP_FILETYPE_XLS
        else if (!strcasecmp(typeExt, "ppt")) filetype = LIBMTP_FILETYPE_PPT
        else if (!strcasecmp(typeExt, "mht")) filetype = LIBMTP_FILETYPE_MHT
        else if (!strcasecmp(typeExt, "jp2")) filetype = LIBMTP_FILETYPE_JP2
        else if (!strcasecmp(typeExt, "jpx")) filetype = LIBMTP_FILETYPE_JPX
        else if (!strcasecmp(typeExt, "bin")) filetype = LIBMTP_FILETYPE_FIRMWARE
        else if (!strcasecmp(typeExt, "vcf")) filetype = LIBMTP_FILETYPE_VCARD3
        else /* Tagging as unknown file type */ filetype = LIBMTP_FILETYPE_UNKNOWN
        filetype
      }

      private val copyCb = new LIBMTP_progressfunc_t {
        override def apply(sent: Long, total: Long, data: Pointer[_]): Int = {
          val p = sent.toDouble / total * 100
          print("\r\t%.2f%%".format(p))
          if (p == 100)
            println()
          return 0
        }
      }
    }
  }

}
