package akkaDocuments.pub_sub.chatroom

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}

/**
  * Created by fybai on 06/01/2017.
  */

class ChatClient(name: String) extends Actor {
  /**
    * DistributedPubSubMediator actor应该在集群中的所有节点上都开启，或者相同角色的所有节点
    * DistributedPubSubMediator actor管理着actor注册，然后备份这些条目到集群中所有的actors上，或者相同角色的所有节点上
    *
    * 向mediator注册的信息最终会达到一致，将在几秒后复制到其他所有节点，通过gossip协议扩散到其他节点
    *
    * You can send messages via the mediator on any node to registered actors on any other node.
    *
    * 具有两种模式发送消息，Publish与Send
    *
    * 以下这种方式启动mediator只是作为DistributedPubSub的一个扩展，mediator也可以作为一个普通的actor存在
    */
  val mediator = DistributedPubSub(context.system).mediator
  val topic = "chartoom"

  /**
    * Actor将注册到一个命名的topic。这将导致很多在每个节点上具有很有的订阅者，消息将传送给订阅了该topic的所有订阅者
    */
  mediator ! Subscribe(topic, self)
  println(s"$name joined chat room")

  override def receive: Receive = {
    case ChatClient.Publish(msg) =>
      /**
        * 通过发送Publish消息给本地的mediator来发布消息
        */
      mediator ! Publish(topic, ChatClient.Message(name, msg))
    case ChatClient.Message(from, text) =>
      val direction = if (sender() == self) ">>>>" else s"<< $from:"
      println(s"$name $direction $text")
  }
}

object ChatClient {
  def props(name: String): Props = {
    Props(classOf[ChatClient], name)
  }

  case class Publish(msg: String)
  case class Message(from: String, text: String)
}
