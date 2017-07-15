package com.t660.mtp_sync

/**
  * Created by luyi on 15/07/2017.
  */
trait IFile {
  def path: Seq[String]

  def name: String = path.last

  def isFolder: Boolean

  def size: Long

  def modificationDate: Long

  def children: Seq[IFile]
}

object IFile {

  def print(file: IFile, indent: String = ""): Unit = {
    println(indent + file.name + "\t" + file.size)
    if (file.isFolder) {
      for (c <- file.children)
        print(c, indent + "  ")
    }
  }

}
