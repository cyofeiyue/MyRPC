package com.feiyue.rpc

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory


class Master extends Actor{

  println("Master:constructor invoked")

  override def preStart(): Unit = {
    println("Master:preStart invoked")
  }

  // 用于接收消息
  override def receive: Receive = {
    case "connect" => {
      println("Master:a client connected")
      sender ! "reply"
    }

    case "test" => {
      println("Master:test")
    }

  }

}

 object Master{
  def main(args: Array[String]): Unit = {
    val host = args(0)
    val port = args(1).toInt

    //Master 配置
    val configStr =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = "$host"
         |akka.remote.netty.tcp.port = "$port"
       """.stripMargin

    val config = ConfigFactory.parseString(configStr)

    //ActorSystem老大，辅助创建和监控下面的Actor，他是单例的
    val actorSystem = ActorSystem("MasterSystem",config)

    //创建Actor, 起个名字：MasterSystem
    //Master主构造器会执行
    val master = actorSystem.actorOf(Props[Master],"Master")

    //发送信息
    master ! "test"

    //让进程等待着, 先别结束
    actorSystem.awaitTermination()


  }
}
