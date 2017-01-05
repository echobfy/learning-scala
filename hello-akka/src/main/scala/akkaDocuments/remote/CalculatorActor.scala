package akkaDocuments.remote

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
  * Created by fybai on 05/01/2017.
  */
class CalculatorActor extends Actor {
  override def receive: Receive = {
    case Add(n1, n2) =>
      println("Calculating %d + %d".format(n1, n2))
      sender() ! AddResult(n1, n2, n1 + n2)
    case Subtract(n1, n2) =>
      println("Calculating %d - %d".format(n1, n2))
      sender() ! SubtractResult(n1, n2, n1 - n2)
    case Multiply(n1, n2) =>
      println("Calculating %d * %d".format(n1, n2))
      sender() ! MultiplicationResult(n1, n2, n1 * n2)
    case Divide(n1, n2) =>
      println("Calculating %.0f / %d".format(n1, n2))
      sender() ! DivisionResult(n1, n2, n1 / n2)
  }
}
