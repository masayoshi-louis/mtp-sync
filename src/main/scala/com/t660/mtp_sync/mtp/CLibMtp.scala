package com.t660.mtp_sync.mtp

import java.io.File

import com.t660.mtp_sync._
import com.t660.mtp_sync.util.PathUtils
import libmtp.LibmtpLibrary.LIBMTP_error_number_t.{LIBMTP_ERROR_NONE, LIBMTP_ERROR_NO_DEVICE_ATTACHED}
import libmtp.LibmtpLibrary.LIBMTP_filetype_t.LIBMTP_FILETYPE_FOLDER
import libmtp.LibmtpLibrary._
import libmtp._
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
        case _ => //ignore
      }

      override def copy = (src: IFile, dst: Seq[String]) => src match {
        case localSrc: LocalFile => {
          ???
        }
        case mtpSrc: MtpFile => {
          val dstPath = PathUtils.stringify(dst)
          val dstPathBuff = Pointer.allocateBytes((dstPath.length + 1) * 4)
          val ret = try {
            dstPathBuff.setCString(dstPath)
            LIBMTP_Get_File_To_File(devicePtr, mtpSrc.id, dstPathBuff, null, null)
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

      private[this] def find(path: Seq[String]): Option[MtpFile] = pathsMap.get(path)
    }
  }

}
