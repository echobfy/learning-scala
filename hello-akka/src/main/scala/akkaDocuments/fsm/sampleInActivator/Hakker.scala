package akkaDocuments.fsm.sampleInActivator

import akka.actor.{ActorRef, FSM}

import scala.concurrent.duration._

/**
  * Created by fybai on 01/01/2017.
  */
class Hakker(name: String, left: ActorRef, right: ActorRef) extends FSM[Hakker.FSMHakkerState, Hakker.TakenChopsticks] {
  import Hakker._
  import Chopstick._

  startWith(Waiting, TakenChopsticks(None, None))

  when(Waiting) {
    case Event(Think, _) =>
      println("%s starts to think".format(name))
      startThinking(1.seconds)
  }

  when(Thinking) {
    case Event(StateTimeout, _) =>
      left ! Take
      right ! Take
      goto(Hungry)
  }

  when(Hungry) {
    case Event(Taken(`left`), _) =>
      goto(WaitForOtherChopstick) using TakenChopsticks(Some(left), None)
    case Event(Taken(`right`), _) =>
      goto(WaitForOtherChopstick) using TakenChopsticks(None, Some(right))
    case Event(Busy(_), _) =>
      goto(FirstChopstickDenied)
  }

  when(WaitForOtherChopstick) {
    case Event(Taken(`left`), TakenChopsticks(None, Some(`right`))) => startEating(left, right)
    case Event(Taken(`right`), TakenChopsticks(Some(`left`), None)) => startEating(left, right)
    case Event(Busy(chopstick), TakenChopsticks(leftOption, rightOption)) =>
      leftOption.foreach(_ ! Put)
      rightOption.foreach(_ ! Put)
      startThinking(10.milliseconds)
  }

  when(FirstChopstickDenied) {
    case Event(Taken(secondChopstick), _) =>
      secondChopstick ! Put
      startThinking(10.milliseconds)
    case Event(Busy(chopstick), _) =>
      startThinking(10.milliseconds)
  }

  when(Eating) {
    case Event(StateTimeout, _) =>
      println("%s puts down his chopsticks and starts to think".format(name))
      left ! Put
      right ! Put
      startThinking(5.seconds)
  }

  private def startThinking(duration: FiniteDuration): State = {
    goto(Thinking) using TakenChopsticks(None, None) forMax duration
  }

  private def startEating(left: ActorRef, right: ActorRef): State = {
    println("%s has picked up %s and %s and starts to eat".format(name, left.path.name, right.path.name))
    goto(Eating) using TakenChopsticks(Some(left), Some(right)) forMax 1.seconds
  }

  initialize()
}


object Hakker {

  /**
    * 哲学家Actor可以收到的消息
    */
  sealed trait FSMHakkerMessage
  object Think extends FSMHakkerMessage

  /**
    * 哲学家所处的可能状态
    */
  sealed trait FSMHakkerState
  case object Waiting extends FSMHakkerState
  case object Thinking extends FSMHakkerState
  case object Hungry extends FSMHakkerState
  case object WaitForOtherChopstick extends FSMHakkerState
  case object FirstChopstickDenied extends FSMHakkerState
  case object Eating extends FSMHakkerState

  /**
    * 哲学家Actor包含的状态数据
    */
  final case class TakenChopsticks(left: Option[ActorRef], right: Option[ActorRef])
}
