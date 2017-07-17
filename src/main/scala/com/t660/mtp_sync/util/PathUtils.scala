package com.t660.mtp_sync.util

import java.nio.file.Paths

import org.apache.commons.io.FilenameUtils

/**
  * Created by luyi on 15/07/2017.
  */
object PathUtils {

  def absolute(str: String): String = Paths.get(str).toAbsolutePath.toString

  def parse(str: String): Seq[String] = {
    val tmp = FilenameUtils.normalize(str).split("/")
    if (tmp.length == 0) Seq.empty
    else if (tmp(0) == "") tmp.drop(1)
    else tmp
  }

  def stringify(path: Seq[String]) = path.mkString("/", "/", "")

}
