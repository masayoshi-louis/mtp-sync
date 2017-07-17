package com.t660.mtp_sync.diff

import java.io.PrintStream

import com.t660.mtp_sync.IFile
import com.t660.mtp_sync.util.PathUtils

/**
  * Created by luyi on 16/07/2017.
  */
sealed trait Operation

case class CreateFolder(path: Seq[String]) extends Operation

case class AddFile(src: IFile, dst: Seq[String]) extends Operation

case class Remove(file: IFile) extends Operation

case class UpdateFile(src: IFile, dst: IFile) extends Operation

object Operation {
  def printer(out: PrintStream)(op: Operation): Unit = print(op, out)

  def print(op: Operation, out: PrintStream): Unit = op match {
    case CreateFolder(p) => out.println(s"CreateFolder: \n\tpath: ${PathUtils.stringify(p)}")
    case AddFile(s, d) => out.println(s"Copy: \n\tsrc: ${PathUtils.stringify(s.path)}\n\tdst: ${PathUtils.stringify(d)}")
    case Remove(f) => out.println(s"Delete: \n\tpath: ${PathUtils.stringify(f.path)}")
    case UpdateFile(s, d) => out.println(s"Copy: \n\tsrc: ${PathUtils.stringify(s.path)}\n\tdst: ${PathUtils.stringify(d.path)}")
  }
}