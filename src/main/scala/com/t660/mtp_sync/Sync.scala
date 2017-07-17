package com.t660.mtp_sync

/**
  * Created by luyi on 17/07/2017.
  */
class Sync(dstFs: FileSystem, copy: FileCopy) {

  def apply(diffResult: diff.Result): Unit = {
    for (r <- diffResult.removes)
      dstFs.remove(r.file)
    for (c <- diffResult.creates)
      dstFs.mkdir(c.path)
    for (u <- diffResult.updates) {
      dstFs.remove(u.dst)
      copy(u.src, u.dst.path)
    }
    for (a <- diffResult.adds)
      copy(a.src, a.dst)
  }

}
