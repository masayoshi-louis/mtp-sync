package com.t660.mtp_sync.mtp

import libmtp.LibmtpLibrary.LIBMTP_error_number_t.{LIBMTP_ERROR_NONE, LIBMTP_ERROR_NO_DEVICE_ATTACHED}
import libmtp.LibmtpLibrary.LIBMTP_filetype_t.LIBMTP_FILETYPE_FOLDER
import libmtp.LibmtpLibrary._
import libmtp._
import org.bridj.{IntValuedEnum, Pointer}

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

/**
  * Created by luyi on 15/07/2017.
  */
object CLibMtp {

  LIBMTP_Init()

  var err: IntValuedEnum[LibmtpLibrary.LIBMTP_error_number_t] = _

  val (rawDevices, numDevices) = {
    val rawDevicesPtr = Pointer.allocatePointer(classOf[LIBMTP_raw_device_struct])
    val numDevicesPtr = Pointer.allocateInt
    err = LIBMTP_Detect_Raw_Devices(rawDevicesPtr, numDevicesPtr)
    if (err eq LIBMTP_ERROR_NO_DEVICE_ATTACHED) {
      throw new RuntimeException("no device attached!")
    } else if (err eq LIBMTP_ERROR_NONE) { // OK
      (rawDevicesPtr.get, numDevicesPtr.get())
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
      val ptr = devicePtr.get().storage()

      @tailrec
      def getAll(ptr: Pointer[LIBMTP_devicestorage_struct] = ptr, result: List[DeviceStorage] = Nil): List[DeviceStorage] = {
        if (ptr == null) result else {
          val s = new DeviceStorageImpl(devicePtr, ptr)
          getAll(ptr.get().next(), s :: result)
        }
      }

      getAll().reverse
    }
  }

  private class DeviceStorageImpl(devicePtr: Pointer[LIBMTP_mtpdevice_struct],
                                  storagePtr: Pointer[LIBMTP_devicestorage_struct]) extends DeviceStorage {
    override def description: String = storagePtr.get().StorageDescription().getCString

    override def id: Int = storagePtr.get().id()

    override def listFiles(fromPath: Seq[String]): Seq[MtpFile] = {
      val storageId = storagePtr.get().id()
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
          val fileStruct = filePtr.get()
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

      listFiles(Seq.empty, LIBMTP_FILES_AND_FOLDERS_ROOT.asInstanceOf[Int])
    }
  }

}
