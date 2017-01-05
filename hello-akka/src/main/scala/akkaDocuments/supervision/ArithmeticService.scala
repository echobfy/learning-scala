package akkaDocuments.supervision

import akka.actor.Actor.Receive
import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Status}
import akkaDocuments.supervision.FlakyExpressionCalculator.FlakinessException

/**
  * Created by fybai on 03/01/2017.
  */
class ArithmeticService extends Actor with ActorLogging {
  import FlakyExpressionCalculator.{Left, Result, Right}

  var pendingWorkers = Map[ActorRef, ActorRef]()

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case _: FlakinessException =>
      log.warning("Evaluation of a top level expression failed, restarting.")
      Restart
    case e: ArithmeticException =>
      log.error("Evaluation failed because of: {}", e.getMessage)
      notifyConsumerFailure(worker = sender(), failure = e)
      Stop
    case e =>
      log.error("Unexpected failure: {}", e.getMessage)
      notifyConsumerFailure(worker = sender(), failure = e)
      Stop
  }

  def notifyConsumerFailure(worker: ActorRef, failure: Throwable): Unit = {
    pendingWorkers.get(worker) foreach { _ ! Status.Failure(failure)}
    pendingWorkers -= worker
  }

  def notifyConsumerSuccess(worker: ActorRef, result: Int): Unit = {
    pendingWorkers.get(worker) foreach { _ ! result}
    pendingWorkers -= worker
  }

  override def receive: Receive = {
    case e: Expression =>
      val worker = context.actorOf(FlakyExpressionCalculator.props(expr = e, position = Left))
      pendingWorkers += worker -> sender()

    case Result(originalExpression, value, _) =>
      notifyConsumerSuccess(worker = sender(), result = value)
  }
}
