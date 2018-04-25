package com.feiyue.rpc

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable
import scala.concurrent.duration._


class Master(val host: String, val port: Int) extends Actor{

  val CHECK_INTERVAL = 15000

  // workerId -> WorkerInfo
  val idToWorkers = new mutable.HashMap[String, WorkerInfo]()
  // WorkerInfo
  val workers = new mutable.HashSet[WorkerInfo]()

  println("Master:constructor invoked")

  override def preStart(): Unit = {
    println("Master:preStart invoked")

    import context.dispatcher
    context.system.scheduler.schedule(0 millis, CHECK_INTERVAL millis, self, CheckTimeOutWorker)
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

    case RegisterWorker(workerId, memory, cores) => {
      if(!idToWorkers.contains(workerId)) {
        val workerInfo =  new WorkerInfo(workerId,memory,cores)
        idToWorkers(workerId) = workerInfo
        workers += workerInfo

        //通知worker注册
        sender ! RegisteredWorker(s"akka.tcp://MasterSystem@$host:$port/user/Master")
      }
    }

    case Heartbeat(id) => {
      if(idToWorkers.contains(id)){
        val workerInfo = idToWorkers(id)
        //报活
        workerInfo.lastTimeHeartBeat = System.currentTimeMillis()
      }

    }

    case CheckTimeOutWorker => {
      val currentTime = System.currentTimeMillis()
      val toRemoves = workers.filter(x => currentTime - x.lastTimeHeartBeat > CHECK_INTERVAL)
      for(i <- toRemoves){
        workers -= i
        idToWorkers.remove(i.id)
      }

      println("和Master相连的Worker还有：" + workers.size)
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
    //val master = actorSystem.actorOf(Props[Master],"Master")

    val master = actorSystem.actorOf(Props(new Master(host, port)),"Master")
    //发送信息
    master ! "test"

    //让进程等待着, 先别结束
    actorSystem.awaitTermination()


  }
}
