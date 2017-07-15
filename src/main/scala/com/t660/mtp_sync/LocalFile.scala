package com.t660.mtp_sync

import java.io.File

/**
  * Created by luyi on 15/07/2017.
  */
case class LocalFile(path: Seq[String],
                     isFolder: Boolean,
                     size: Long,
                     modificationDate: Long,
                     children: Seq[LocalFile] = Seq.empty) extends IFile

object LocalFile {

  def listFiles(fromPath: Seq[String]): Seq[LocalFile] = {

    def list(path: Seq[String])(jFile: File): LocalFile = {
      require(jFile.exists())
      val children = if (jFile.isDirectory) {
        jFile.listFiles().toSeq.map(list(path))
      } else {
        Seq.empty
      }
      LocalFile(
        path = path :+ jFile.getName,
        isFolder = jFile.isDirectory,
        size = jFile.length(),
        modificationDate = jFile.lastModified(),
        children = children
      )
    }

    Seq(list(fromPath)(new File(fromPath.mkString("/"))))
  }

}
