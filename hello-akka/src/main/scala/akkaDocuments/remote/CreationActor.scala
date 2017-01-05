package akkaDocuments.remote

import akka.actor.{Actor, Props}
import akka.actor.Actor.Receive

/**
  * Created by fybai on 05/01/2017.
  */

/**
  * CreationActor对于每个收到的MathOp都创建一个远程的孩子CalculatorActor
  * 在配置文件remotecreation.conf中的deployment节匹配了这些的孩子actor，定义了这些actors将被部署在远程节点上
  *
  * 错误处理（即监控策略）与本地孩子actor的工作方式一样
  * 除此之外，如果网络错误，或者JVM崩溃，孩子actor将停止并且自动从parent中移除，即使他们是处在不同的机器上
  */
class CreationActor extends Actor {
  override def receive: Receive = {
    case op: MathOp =>
      val calculator = context.actorOf(Props[CalculatorActor])
      calculator ! op
    case result: MathResult => result match {
      case MultiplicationResult(n1, n2, r) =>
        printf("Mul result: %d * %d = %d\n", n1, n2, r)
        context.stop(sender())
      case DivisionResult(n1, n2, r) =>
        printf("Div result: %.0f / %d = %.2f\n", n1, n2, r)
        context.stop(sender())
    }
  }
}
