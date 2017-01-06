package akkaDocuments.remoting

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.util.Random

/**
  * Created by fybai on 05/01/2017.
  *
  * activator "run-main hello-akka.src.main.scala.akkaDocuments.remote.CreationApplication CalculatorWorker"
  * activator "run-main hello-akka.src.main.scala.akkaDocuments.remote.CreationApplication Creation"
  */

/**
  * Akka提供了两种使用remoting的方法：
  *   1. Lookup： 使用actorSelection(path)查找一个远程节点上的actor            --> 即远程查找actor
  *   2. Creation：使用actorOf(Props(...), actorName)创建一个远程节点上的actor --> 即远程部署actor
  *
  *
  * 该例子中包含了两个actor systems：
  *   1. CalculatorWorkerSystem监听在2552端口，注意它不启动Actor
  *   2. CreationSystem监听在2554端口，然后开启一个actor（CreationActor），然后该Actor在CalculatorWorkerSystem中创建远程的calculator worker，再发送计算任务给他们
  */

object CreationApplication {

  def main(args: Array[String]): Unit = {
    /**
      * 开启两个actor systems，运行在同一个JVM上，但是同样可以运行在多个JVM上
      */
    if (args.isEmpty || args.head == "CalculatorWorker")
      startRemoteWorkerSystem()
    if (args.isEmpty || args.head == "Creation")
      startRemoteCreationSystem()
  }

  def startRemoteWorkerSystem(): Unit = {
    ActorSystem("CalculatorWorkerSystem", ConfigFactory.load("calculator"))
    println("Started CalculatorWorkerSystem")
  }

  def startRemoteCreationSystem(): Unit = {
    /**
      * 加载remotecreation.conf，声明/creationActor/`*`的Actors将在远程节点上创建
      */
    val system = ActorSystem("CreationSystem", ConfigFactory.load("remotecreation.conf"))

    val actor = system.actorOf(Props[CreationActor], name = "creationActor")

    println("Started CreationSystem.")

    import system.dispatcher
    system.scheduler.schedule(1.second, 1.second) {
      if (Random.nextInt(100) % 2 ==0) {
        actor ! Multiply(Random.nextInt(20), Random.nextInt(20))
      } else {
        actor ! Divide(Random.nextInt(10000), Random.nextInt(99) + 1)
      }
    }
  }
}
