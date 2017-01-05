package akkaDocuments.routing

import akka.actor.{Actor, ActorSystem, PoisonPill, Props}
import akka.actor.Actor.Receive
import akka.event.Logging
import akka.routing.{Broadcast, FromConfig}

/**
  * Created by fybai on 04/01/2017.
  */
class SimpleRouting extends Actor {
  import SimpleRouting._

  val log = Logging(context.system, this)

  override def receive: Receive = {
    case Message(msg) => log.info("Got a valid message: %s".format(msg))
    case default => log.error("Got a message I don't understand.")
  }
}

object SimpleRouting {
  case class Message(msg: String)
}


object SimpleRouterSetup extends App {
  val system = ActorSystem("SimpleSystem")
  val simpleRouted = system.actorOf(Props[SimpleRouting].withRouter(FromConfig()), name = "simpleRoutedActor")

  /**
    * 这里的使用的Router，只会将消息路由到单个的routee上，不可能广播消息
    */
  for (n <- 1 until 100) {
    simpleRouted ! SimpleRouting.Message("Hello, Akka #%d!".format(n))
  }

  /**
    * 完成处理你目前队列中的消息，然后就可以挂了 ===> PoisonPill提供的语义
    * 使用特殊的样例类Broadcast，当一个Router受到一个Broadcast时，将其解封装，然后路由到所有的routee中
    */
  simpleRouted ! Broadcast(PoisonPill)
  simpleRouted ! Broadcast(SimpleRouting.Message("Hello, EveryBody!"))
  /**
    * 注意发送消息是异步的，即log消息可能还没打印出来，程序就已经执行了下面这句话
    */
  System.err.println("Finished sending messages to Router.")
}
