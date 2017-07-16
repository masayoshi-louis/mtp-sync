package com.t660.mtp_sync

import com.t660.mtp_sync.mtp.CLibMtp
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
      val toMtp = new Subcommand("to-mtp")
      val fromMtp = new Subcommand("from-mtp")
      addSubcommand(toMtp)
      addSubcommand(fromMtp)
    }
    addSubcommand(sync)
    verify()
  }

  Opts.subcommands match {
    case Opts.storage :: Opts.storage.list :: Nil => {
      val device = CLibMtp.openDevice(Opts.device())
      for (s <- device.storages) {
        println(s"id=${s.id}, description=${s.description}")
      }
    }
    case Opts.sync :: sub :: Nil => {
      // TODO
      sub match {
        case Opts.sync.toMtp => {
          println("to")
        }
        case Opts.sync.fromMtp => {
          println("from")
        }
      }
    }
  }

}
