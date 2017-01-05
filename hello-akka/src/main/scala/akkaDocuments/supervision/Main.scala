package akkaDocuments.supervision

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by fybai on 03/01/2017.
  */
object Main {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("calculator-system")
    val caculatorService = system.actorOf(Props[ArithmeticService], "arithmetic-service")

    def calculate(expr: Expression): Future[Int] = {
      implicit val timeout = Timeout(1.second)
      (caculatorService ? expr).mapTo[Int]
    }

    val task = Divide(
      Add(Const(3), Const(5)),
      Multiply(
        Const(5),
        Add(null, Const(1))
      )
    )

    val result = Await.result(calculate(task), 1.second)
    println(s"Got result: $result")

//    Await.ready(system.terminate(), Duration.Inf)
  }
}
