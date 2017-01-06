package akkaDocuments.remoting

import akka.actor.{Actor, ActorIdentity, ActorRef, Identify, ReceiveTimeout, Terminated}
import akka.actor.Actor.Receive

import scala.concurrent.duration._

/**
  * Created by fybai on 05/01/2017.
  */
class LookupActor(path: String) extends Actor {

  sendIdentifyRequest()

  /**
    * 第一步先发送Identify消息给该path的actor selection，远程的actor将会回复一个ActorIdentity，其中包含了它的ActorRef
    *
    * Identify消息是一个内嵌的消息，所有的Actor都理解该消息，也会自动回复ActorIdentity
    * 如果该认证失败，LookupActor将会在一定时间后重试
    */
  def sendIdentifyRequest(): Unit = {
    context.actorSelection(path) ! Identify(path)

    import context.dispatcher
    context.system.scheduler.scheduleOnce(3.seconds, self, ReceiveTimeout)
  }

  override def receive: Receive = identifying

  /**
    * 一旦拥有远程服务的ActorRef，就可以watch它。
    */
  def identifying: Receive = {
    case ActorIdentity(`path`, Some(actor)) =>
      context.watch(actor)
      context.become(active(actor))
    case ActorIdentity(`path`, None) => println(s"Remote actor not available: $path")
    case ReceiveTimeout => sendIdentifyRequest()
    case _ => println("Not ready yet")
  }

  /**
    * 远程系统可能会重启，Terminated消息将会接收到，然后重新开始认证过程，建立一个连接到新的远程系统
    */
  def active(actor: ActorRef): Receive = {
    case op: MathOp => actor ! op
    case result: MathResult => result match {
      case AddResult(n1, n2, r) =>
        printf("Add result: %d + %d = %d\n", n1, n2, r)
      case SubtractResult(n1, n2, r) =>
        printf("Sub result: %d - %d = %d\n", n1, n2, r)
    }
    case Terminated(`actor`) =>
      println("Calculator terminated")
      sendIdentifyRequest()
      context.become(identifying)
    case ReceiveTimeout =>
  }

}
