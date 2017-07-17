package com.t660.mtp_sync.mtp

import com.t660.mtp_sync.{FileCopyComponent, FileSystem}

/**
  * Created by luyi on 15/07/2017.
  */
trait DeviceStorage {

  def id: Int

  def description: String

  def listFiles(fromPath: Seq[String]): MtpFile

  def getFs(bindToRoot: MtpFile): FileSystem with FileCopyComponent

}
