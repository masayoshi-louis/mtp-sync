package com.t660.mtp_sync.mtp

/**
  * Created by luyi on 15/07/2017.
  */
trait MtpDevice {

  def storages: Seq[DeviceStorage]

}
