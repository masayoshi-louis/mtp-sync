package com.t660.mtp_sync

/**
  * Created by luyi on 17/07/2017.
  */
trait FileSystem {

  def remove(file: IFile): Unit

  def mkdir(path: Seq[String])

}
