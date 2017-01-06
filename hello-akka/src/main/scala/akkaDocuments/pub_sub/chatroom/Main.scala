package akkaDocuments.pub_sub.chatroom

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory

/**
  * Created by fybai on 06/01/2017.
  */

/**
  * 当actors注册了一些感兴趣的话题，发送消息给集群中所有的actors，即使这些actors不知道在何处
  *
  *
  * Send模式：
  *   这是一种点对点模式，每条信息发送到一个目的地，但是你还是不用知道目的地位于何处
  *   消息将传送给一个路径匹配的接受者，如果有注册上的。如果多个注册者匹配，则随机发送给一个目标
  *   典型的使用场景，是用于广播消息到所有actor system上面同样path的actors，这些actors往往是冗余的
  */
object Main {

  def main(args: Array[String]): Unit = {
    val systemName = "ChatApp"
    val system1 = ActorSystem(systemName, ConfigFactory.load("chatroom"))
    val joinAddress = Cluster(system1).selfAddress

    Cluster(system1).join(joinAddress)
    system1.actorOf(Props[MemberListener], "memberListener")
    system1.actorOf(Props[RandomUser], "Ben")
    system1.actorOf(Props[RandomUser], "Kathy")

    Thread.sleep(5000)
    val system2 = ActorSystem(systemName, ConfigFactory.load("chatroom"))
    Cluster(system2).join(joinAddress)
    system2.actorOf(Props[RandomUser], "Skye")

    Thread.sleep(10000)
    val system3 = ActorSystem(systemName, ConfigFactory.load("chatroom"))
    Cluster(system3).join(joinAddress)
    system3.actorOf(Props[RandomUser], "Miguel")
    system3.actorOf(Props[RandomUser], "Tyler")
  }
}
