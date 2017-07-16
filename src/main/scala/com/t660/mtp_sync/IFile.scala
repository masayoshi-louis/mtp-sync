package com.t660.mtp_sync

import scala.collection.mutable.ArrayBuffer

/**
  * Created by luyi on 15/07/2017.
  */
trait IFile {
  def path: Seq[String]

  def name: String = if (path.isEmpty) "/" else path.last

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

  def flatten(single: IFile): Seq[IFile] = flatten(Array(single))

  def flatten(list: TraversableOnce[IFile]): Seq[IFile] = {
    val buff = new ArrayBuffer[IFile]()

    def flatten(f: IFile): Unit = {
      buff += f
      if (f.isFolder) f.children.foreach(flatten)
    }

    list.foreach(flatten)

    buff
  }

}
