package akkaDocuments.logging

import akka.actor.{Actor, ActorLogging}
import akka.actor.Actor.Receive
import akka.event.Logging

/**
  * Created by fybai on 06/01/2017.
  */
class LoggingActor extends Actor {

  /**
    * 使用该方式创建一个LoggingAdapter
    * 注意：混入ActorLogging具有一样的效果，详见ActorLogging的源码
    *
    * Logging的第二个参数表示该logging通道的来源，将会翻译成String类型
    */
  val log = Logging(context.system, this)

  override def preStart(): Unit = {
    log.debug("Starting")
  }

  override def receive: Receive = ???
}
