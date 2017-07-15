package com.t660.mtp_sync.mtp

import libmtp.LibmtpLibrary.LIBMTP_error_number_t.{LIBMTP_ERROR_NONE, LIBMTP_ERROR_NO_DEVICE_ATTACHED}
import libmtp.LibmtpLibrary._
import libmtp.{LIBMTP_devicestorage_struct, LIBMTP_mtpdevice_struct, LIBMTP_raw_device_struct, LibmtpLibrary}
import org.bridj.{IntValuedEnum, Pointer}

import scala.annotation.tailrec

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
          val s = new DeviceStorageImpl(ptr)
          getAll(ptr.get().next(), s :: result)
        }
      }

      getAll().reverse
    }
  }

  private class DeviceStorageImpl(ptr: Pointer[LIBMTP_devicestorage_struct]) extends DeviceStorage {
    override def description: String = ptr.get().StorageDescription().getCString

    override def id: Int = ptr.get().id()
  }

}
