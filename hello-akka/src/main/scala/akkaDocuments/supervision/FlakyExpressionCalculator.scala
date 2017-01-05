package akkaDocuments.supervision

import java.util.concurrent.ThreadLocalRandom

import akka.actor.Actor.Receive
import akka.actor.SupervisorStrategy.{Escalate, Restart}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import FlakyExpressionCalculator._

/**
  * Created by fybai on 03/01/2017.
  */
object FlakyExpressionCalculator {
  def props(expr: Expression, position: Position): Props = {
    Props(classOf[FlakyExpressionCalculator], expr, position)
  }

  trait Position
  case object Left extends Position
  case object Right extends Position
  case class Result(originalExpression: Expression, value: Int, position: Position)

  class FlakinessException extends Exception("Flakiness")
}


class FlakyExpressionCalculator(
                                 val expr: Expression,
                                 val myPosition: Position
                               )
  extends Actor with ActorLogging {


  private var results = Map.empty[Position, Int]
  private var expected = Set[Position](Left, Right)

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case _: FlakinessException =>
      log.warning("Evaluation of {} failed, restarting.", expr)
      Restart
    case _ =>
      Escalate
  }
  /**
    * 丰富的Actor的生命周期钩子函数提供了一个有效的工具来实现不同的初始化模式
    *
    * 在一个ActorRef的生命周期中，一个actor可能会重启多次，老的实例会被新的实例所取代，但是外界只拥有ActorRef来说是不可见的
    *
    * 可以通过三种方式来初始化一个Actor：
    *   1. 通过构造器，每次重启都会生效
    *   2. 通过preStart钩子方法，较构造器灵活，可以通过覆盖postRestart实现只有第一次生效
    *   3. 通过消息传递（谨慎使用，可能会丢失，同时也可能造成在初始化完成之前ActorRef已经暴露，初始化消息之前就接收到了很多用户的消息）
    */
  override def preStart(): Unit = expr match {
    case Const(value) =>
      context.parent ! Result(expr, value, myPosition)
      context.stop(self)
    case _ =>
      context.actorOf(FlakyExpressionCalculator.props(expr.left, Left), name = "left")
      context.actorOf(FlakyExpressionCalculator.props(expr.right, Right), name = "right")
  }

  /**
    * Actor通过在一个ActorRefFactory（ActorContext或者ActorSystem）上调用stop方法来停止
    * 典型情况下，context用来停止actor自己或者孩子actor，而system用来停止顶级的actors。同时注意actor的结束时异步的
    *
    * 在Actor停止后，在其mailbox中的消息将不会被处理。默认情况下这些消息会被发送到ActorSystem的deadLetters，取决于mailbox的实现。
    *
    * 一个Actor的结束过程分为两步：
    *   1. 该actor悬挂起它自己的mailbox，然后发送一个stop命令给其全部的孩子actor，然后在内部处理孩子actor发来的结束通知，直到最后一个
    *   2. 最后停止自身，调用postStop方法，倒掉mailbox，发布Terminated消息在DeathWatch，最后告诉其supervisor
    *
    * 该过程保证了ActorSystem有序的停止，传播stop命令到叶子节点，然后收集他们的确认到需要停止的supervisor。
    * 如果其中一个actor没有响应（处理一个消息过长，未收到stop命令），然后整个进程将会卡住
    *
    * 当ActorSystem.terminate时，system guardian Actors将会停止，之前提到的流程将会保证整个系统的合理结束
    *
    */
  override def receive: Receive = {
    case Result(_, value, position) if expected(position) =>
      expected -= position
      results += position -> value
      if (results.size == 2) {
        flakiness()
        val result: Int = evaluate(expr, results(Left), results(Right))
        log.info("Evaluated expression {} to value {}", expr, result)
        context.parent ! Result(expr, result, myPosition)
        context.stop(self)
      }

    case Result(_, _, position) =>
      throw new IllegalStateException(
        s"Expected results for positions ${expected.mkString(", ")} " +
          s"but got position $position"
      )
  }

  private def evaluate(expr: Expression, left: Int, right: Int): Int = expr match {
    case _: Add => left + right
    case _: Multiply => left * right
    case _: Divide => left / right
  }

  private def flakiness(): Unit = {
    if (ThreadLocalRandom.current().nextDouble() < 0.2)
      throw new FlakinessException
  }
}

