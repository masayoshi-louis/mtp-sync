package com.t660.mtp_sync.mtp

import com.t660.mtp_sync.IFile

/**
  * Created by luyi on 15/07/2017.
  */
case class MtpFile(id: Int,
                   path: Seq[String],
                   isFolder: Boolean,
                   parentId: Int,
                   storageId: Int,
                   size: Long,
                   modificationDate: Long,
                   children: Seq[MtpFile] = Seq.empty) extends IFile
