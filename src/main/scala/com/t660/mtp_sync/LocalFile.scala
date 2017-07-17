package com.t660.mtp_sync

import java.io.File

import com.t660.mtp_sync.util.PathUtils

/**
  * Created by luyi on 15/07/2017.
  */
case class LocalFile(path: Seq[String],
                     isFolder: Boolean,
                     size: Long,
                     modificationDate: Long,
                     children: Seq[LocalFile] = Seq.empty) extends IFile

object LocalFile {

  def listFiles(fromPath: Seq[String]): LocalFile = {

    def list(path: Seq[String], root: Boolean = false)(jFile: File): LocalFile = {
      require(jFile.exists())
      val thisPath = if (root) path else path :+ jFile.getName
      val children = if (jFile.isDirectory) {
        jFile.listFiles().toSeq.map(list(thisPath))
      } else {
        Seq.empty
      }
      LocalFile(
        path = thisPath,
        isFolder = jFile.isDirectory,
        size = jFile.length(),
        modificationDate = jFile.lastModified(),
        children = children
      )
    }

    list(fromPath, true)(new File(PathUtils.stringify(fromPath)))
  }

}
