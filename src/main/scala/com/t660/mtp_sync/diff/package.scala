package com.t660.mtp_sync

import com.t660.mtp_sync.util.PathUtils

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by luyi on 16/07/2017.
  */
package object diff {

  case class Result(removes: Seq[Remove],
                    creates: Seq[CreateFolder],
                    adds: Seq[AddFile],
                    updates: Seq[UpdateFile])

  def compute(srcBase: Seq[String], srcRoot: IFile, dstBase: Seq[String], dstRoot: IFile): Result = {
    val srcFiles = IFile.flatten(srcRoot).filter(_.path.startsWith(srcBase))
    val dstFiles = IFile.flatten(dstRoot).filter(_.path.startsWith(dstBase))
    val srcFilesMap = new mutable.LinkedHashMap[String, IFile]
    val dstFilesMap = new mutable.LinkedHashMap[String, IFile]
    srcFiles.foreach(f => srcFilesMap.put(PathUtils.string(f.path.drop(srcBase.size)), f))
    dstFiles.foreach(f => dstFilesMap.put(PathUtils.string(f.path.drop(dstBase.size)), f))
    compute(srcBase, srcFilesMap, dstBase, dstFilesMap)
  }

  private def compute(srcBase: Seq[String], srcFilesMap: mutable.Map[String, IFile],
                      dstBase: Seq[String], dstFilesMap: mutable.Map[String, IFile]): Result = {
    val removes = new ListBuffer[Remove]
    val creates = new ListBuffer[CreateFolder]
    val adds = new ListBuffer[AddFile]
    val updates = new ListBuffer[UpdateFile]
    dstFilesMap foreach {
      case (p, df) => {
        if (!srcFilesMap.contains(p)) removes += Remove(df)
        else {
          val sf = srcFilesMap(p)
          if ((sf.isFolder && !df.isFolder) || (!sf.isFolder && df.isFolder))
            removes += Remove(df)
        }
      }
    }
    srcFilesMap foreach {
      case (p, sf) => if (!dstFilesMap.contains(p)) {
        val newPath = dstBase ++ PathUtils.parse(p)
        if (sf.isFolder)
          creates += CreateFolder(newPath)
        else
          adds += AddFile(sf, newPath)
      }
    }
    Result(removes.toList.reverse, creates.toList, adds.toList, updates.toList)
  }

}
