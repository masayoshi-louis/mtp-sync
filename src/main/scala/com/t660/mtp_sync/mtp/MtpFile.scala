package com.t660.mtp_sync.mtp

/**
  * Created by luyi on 15/07/2017.
  */
case class MtpFile(id: Int,
                   path: Seq[String],
                   isFolder: Boolean,
                   parentId: Int,
                   storageId: Int,
                   modificationDate: Long,
                   children: Seq[MtpFile] = Seq.empty) {
  def name: String = path.last
}

object MtpFile {

  def print(file: MtpFile, indent: String = ""): Unit = {
    println(indent + file.name)
    if (file.isFolder) {
      for (c <- file.children)
        print(c, indent + "  ")
    }
  }

}