package com.t660.mtp_sync

import java.io.File

import com.t660.mtp_sync.util.PathUtils

/**
  * Created by luyi on 17/07/2017.
  */
class LocalFileSystem extends FileSystem {
  override def remove(f: IFile): Unit = {
    val file = new File(PathUtils.stringify(f.path))
    if (file.exists() && !file.delete())
      throw new RuntimeException("failed to delete " + file)
  }

  override def mkdir(path: Seq[String]): Unit = {
    val file = new File(PathUtils.stringify(path))
    if (!file.exists() && !file.mkdir())
      throw new RuntimeException("failed to mkdir " + file)
  }
}
