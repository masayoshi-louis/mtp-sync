package com.t660.mtp_sync

/**
  * Created by luyi on 15/07/2017.
  */
case class LocalFile(path: Seq[String],
                     isFolder: Boolean,
                     size: Long,
                     modificationDate: Long,
                     children: Seq[LocalFile] = Seq.empty) extends IFile
