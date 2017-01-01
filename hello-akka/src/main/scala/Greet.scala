import akka.actor.{Actor, ActorSystem, Inbox, Props}

import scala.concurrent.duration._

/**
  * Created by fybai on 29/12/2016.
  */


/**
  * case classes and case object:
  *
  * 1). 不可变的，且支持模式匹配
  * 2). 默认情况下已经是可序列化
  */
case object Greet

case class WhoToGreet(who: String)

case class Greeting(message: String)


/**
  * 一个Actor没有可以调用的公有的API方法
  * 它通过该Actor可以处理的消息来定义其公有的API
  *
  * 一个Actor是Akka中的一个执行单元，Actors封装了状态与行为，所以从某种意义上来讲，他们是面向对象的。
  * 但他们与Java和Scala中通常的对象不太一样，他们有更强的隔离性。
  *
  * 不允许在多个Actors间共享状态，唯一可以观察其他Actor状态的方法就是发送消息询问。
  *
  * 强隔离性原则，基于事件驱动的模型和位置透明使得他们能够方便自然的解决并发难，扩展难的问题
  */
class Greeter extends Actor {   // 在Java中使用UntypedActor强调其接受的消息是无类型的
  var greeting = ""

  override def receive: Receive = {
    case WhoToGreet(who) => greeting = s"Hello, $who"
    case Greet => sender() ! Greeting(greeting)
  }
}


/**
  * 在Akka中不能使用new方法来创建一个Actor实例，要通过一个工厂来创建，类似于Spring中的BeanFactory
  * 该工厂返回的也不是actor实例，只是指向actor实例的一个ActorRef实例
  *
  * 使用非直接引用能增加更多的灵活性和功能。比如位置透明、运行时优化、"let it carsh"模型等
  */
object Greeter extends App {

  val system = ActorSystem("helloaaka")
  val greeter = system.actorOf(Props[Greeter], "greeter")

  val inbox = Inbox.create(system)

  greeter ! WhoToGreet("akka")

  inbox.send(greeter, Greet)

  val Greeting(message) = inbox.receive(5.seconds)
  println(s"Greeting: $message")
}



