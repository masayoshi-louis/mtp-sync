package com.t660.mtp_sync

/**
  * Created by luyi on 17/07/2017.
  */
trait FileCopy {

  def apply(src: IFile, dst: Seq[String]): Unit

}
