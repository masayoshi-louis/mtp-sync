package com.t660.mtp_sync

import java.io.PrintStream

import com.t660.mtp_sync.diff.Operation
import com.t660.mtp_sync.mtp.{CLibMtp, MtpFile}
import com.t660.mtp_sync.util.PathUtils
import org.rogach.scallop.{ScallopConf, Subcommand}

/**
  * Created by luyi on 15/07/2017.
  */
object MtpSyncMain extends App {

  object Opts extends ScallopConf(args) {
    version("1.0.0")
    val device = opt[Int](required = true, default = Some(0), descr = "device number")

    val storage = new Subcommand("storage") {
      val list = new Subcommand("ls")
      addSubcommand(list)
    }
    addSubcommand(storage)

    val sync = new Subcommand("sync") {
      val storage = opt[Int](required = true, default = Some(0), descr = "storage id")
      val src = opt[String](required = true, descr = "source path", noshort = true)
      val dst = opt[String](required = true, descr = "destination path", noshort = true)
      val diffOut = opt[String](name = "diff-out", required = false, descr = "print diff to file instead of stdout", noshort = true)
      val toMtp = new Subcommand("to-mtp")
      val fromMtp = new Subcommand("from-mtp")
      addSubcommand(toMtp)
      addSubcommand(fromMtp)
    }
    addSubcommand(sync)
    verify()
  }

  Opts.verify()

  val device = CLibMtp.openDevice(Opts.device())

  Opts.subcommands match {
    case Opts.storage :: Opts.storage.list :: Nil => {
      for (s <- device.storages) {
        println(s"id=${s.id}, description=${s.description}")
      }
    }
    case Opts.sync :: sub :: Nil => {
      val mtpStorage = device.storages(Opts.sync.storage())
      var mtpRoot: MtpFile = null
      val srcBase = PathUtils.parse(PathUtils.absolute(Opts.sync.src()))
      val dstBase = PathUtils.parse(PathUtils.absolute(Opts.sync.dst()))
      var srcRoot, dstRoot: IFile = null
      var dstFs: FileSystem = null
      var copyComponent: FileCopyComponent = null
      sub match {
        case Opts.sync.toMtp => {
          srcRoot = LocalFile.listFiles(srcBase)
          mtpRoot = mtpStorage.listFiles(dstBase)
          dstRoot = mtpRoot
          val mtpFs = mtpStorage.getFs(mtpRoot)
          dstFs = mtpFs
          copyComponent = mtpFs
        }
        case Opts.sync.fromMtp => {
          mtpRoot = mtpStorage.listFiles(srcBase)
          srcRoot = mtpRoot
          dstRoot = LocalFile.listFiles(dstBase)
          dstFs = new LocalFileSystem
          copyComponent = mtpStorage.getFs(mtpRoot)
        }
      }
      val diffResult = diff.compute(srcBase, srcRoot, dstBase, dstRoot)
      if (diffResult.isEmpty) {
        println("up-to-date")
      } else {
        val sync = new Sync(dstFs, copyComponent.copy)
        val diffOut = Opts.sync.diffOut.map(new PrintStream(_)).getOrElse(System.out)
        diffResult.all.foreach(Operation.printer(diffOut))
        print("Confirm (y/n)?")
        val userInput = scala.io.StdIn.readLine()
        if (userInput.trim.toLowerCase == "y") {
          sync(diffResult)
          println("done")
        }
        else
          println("aborted")
      }
    }
  }

}
