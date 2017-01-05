package akkaDocuments.actor

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
  * Created by fybai on 02/01/2017.
  */

/**
  * Akka支持Actor运行时的消息循环热切换
  * become携带一个PartialFunction[Any, Unit]参数，将作为新的消息handler
  *
  * 但是注意：actor将会还原成最开始的行为，当其被supervisor重启后
  *
  * 使用become方法也可以实现状态机
  *
  * become是替换栈顶的行为
  * 还有一种使用become的行为是压入栈顶，而不进行替换。在该情况下，必须保证pop（unbecome）与push等量，否则会造成内存泄漏，所以该行为不是默认的
  */
class HotSwapActor extends Actor {
  import context._

  def angry: Receive = {
    case "foo" => sender() ! "I am already angry?"
    case "bar" => become(happy)
  }

  def happy: Receive = {
    case "bar" => sender() ! "I am already happy :-)"
    case "foo" => become(angry)
  }

  override def receive: Receive = {
    case "foo" => become(angry)
    case "bar" => become(happy)
  }
}

/**
  * 初始化模式：
  *
  * 在一个ActorRef的生命周期中，一个Actor可能会经理多次的重启，老的实例将被新的实例所代替，但对于外界持有ActorRef来说是不可见的
  *
  */
