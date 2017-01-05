package akkaDocuments.remote

import scala.concurrent.duration._
import scala.util.Random
import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * Created by fybai on 05/01/2017.
  */

/**
  * 该例子中包含两种actor system：
  *   1. CalculatorSystem监听在2552端口，开启一个actor，即CalculatorActor，提供算术运算服务
  *   2. LookupSystem监听在2553端口，开启一个actor，即LookupActor，发送操作给远程算术运算服务
  */
object LookupApplication {

  def main(args: Array[String]): Unit = {
    if (args.isEmpty || args.head == "Calculator")
      startRemoteCalculatorSystem()
    if (args.isEmpty || args.head == "Lookup")
      startRemoteLookupSystem()
  }

  def startRemoteCalculatorSystem(): Unit = {
    val system = ActorSystem("CalculatorSystem", ConfigFactory.load("calculator"))
    system.actorOf(Props[CalculatorActor], "calculator")

    println("Started CalculatorSystem - waiting for messages")
  }

  def startRemoteLookupSystem(): Unit = {
    val system = ActorSystem("LookupSystem", ConfigFactory.load("remotelookup"))

    val remotePath = "akka.tcp://CalculatorSystem@127.0.0.1:2552/user/calculator"
    val actor = system.actorOf(Props(classOf[LookupActor], remotePath), "lookupActor")

    println("Started LookupSystem")
    import system.dispatcher
    system.scheduler.schedule(1.second, 1.second) {
      if (Random.nextInt(100) % 2 == 0)
        actor ! Add(Random.nextInt(100), Random.nextInt(100))
      else
        actor ! Subtract(Random.nextInt(100), Random.nextInt(100))
    }
  }
}
