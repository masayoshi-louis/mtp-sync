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
    verify()
  }

  Opts.subcommands match {
    case Opts.storage :: Opts.storage.list :: Nil => {
      val device = CLibMtp.openDevice(Opts.device())
      for (s <- device.storages) {
        println(s"id=${s.id}, description=${s.description}")
      }
    }
  }

}
