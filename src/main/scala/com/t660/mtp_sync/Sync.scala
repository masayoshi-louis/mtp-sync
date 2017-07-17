package com.t660.mtp_sync

import com.t660.mtp_sync.diff.Operation

/**
  * Created by luyi on 17/07/2017.
  */
class Sync(dstFs: FileSystem, copy: FileCopy) {

  def apply(diffResult: diff.Result): Unit = {
    for (r <- diffResult.removes) {
      Operation.print(r, System.out)
      dstFs.remove(r.file)
    }
    for (c <- diffResult.creates) {
      Operation.print(c, System.out)
      dstFs.mkdir(c.path)
    }
    for (u <- diffResult.updates) {
      Operation.print(u, System.out)
      dstFs.remove(u.dst)
      copy(u.src, u.dst.path)
    }
    for (a <- diffResult.adds) {
      Operation.print(a, System.out)
      copy(a.src, a.dst)
    }
  }

}
