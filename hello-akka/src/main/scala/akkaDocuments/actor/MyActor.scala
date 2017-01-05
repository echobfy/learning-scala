package akkaDocuments.actor

/**
  * Created by fybai on 02/01/2017.
  */

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive
import akka.event.Logging


/**
  * Actor是一些封装了状态和行为的对象，它们之间的交流通过将消息放在收件人的信箱中。
  */
class MyActor extends Actor {
  val log = Logging(context.system, this)

  /**
    * receive定义了一个PartialFunction[Any, Unit]方法
    * 注意： 返回值类型为Unit，如果actor想回复收到的消息， 则需要显示提供
    *
    * receive方法的结果是一个部分应用函数对象，作为actor的初始行为
    * 当actor构造完成后，可以通过Become/Unbecome来改变一个actor的行为
    * @return
    */
  override def receive: Receive = {
    case "text" => log.info("received test")
    case _ => log.info("received unknown message")
  }
}


object MyActor {

  /**
    * Props是一个配置类，用于指定创造actor的选项
    */
  val props = Props[MyActor]
}
