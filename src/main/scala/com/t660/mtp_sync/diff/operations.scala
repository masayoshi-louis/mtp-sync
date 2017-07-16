package com.t660.mtp_sync.diff

import com.t660.mtp_sync.IFile

/**
  * Created by luyi on 16/07/2017.
  */
sealed trait Operation

case class CreateFolder(path: Seq[String]) extends Operation

case class AddFile(src: IFile, dst: Seq[String]) extends Operation

case class Remove(file: IFile) extends Operation

case class UpdateFile(src: IFile, dst: IFile) extends Operation
