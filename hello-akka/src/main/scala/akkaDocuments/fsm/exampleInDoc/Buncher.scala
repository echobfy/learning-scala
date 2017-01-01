package akkaDocuments.fsm.exampleInDoc

import akka.actor.{ActorRef, FSM}
import scala.concurrent.duration._

/**
  * Created by fybai on 01/01/2017.
  */
class Buncher extends FSM[Buncher.State, Buncher.Data] {
  import Buncher._

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(SetTarget(ref), Uninitialized) =>
      stay using Todo(ref, Vector.empty)
  }

  /**
    * Active状态有一个状态时间，意味着如果在一秒钟内没有消息到达，FSM.StateTimeout就会产生
    */
  when(Active, stateTimeout = 1.second) {
    case Event(Flush | StateTimeout, t: Todo) =>
      goto(Idle) using t.copy(queue = Vector.empty)
  }

  whenUnhandled {
    case Event(Queue(obj), t @ Todo(_, v)) =>
      goto(Active) using t.copy(queue = v :+ obj)

    case Event(e, s) =>
      log.warning("received unhandled request {} in state {}/{}", e, stateName, s)
      stay
  }

  /**
    * 在状态改变时，老的状态数据可以通过stateData来访问，而新的状态数据通过nextStateData来访问
    *
    * 状态保持不变（当前状态为S），可以使用goto(S)或stay()。两者之间的差别在于goto(S)会发出一个S->S事件，而stay()不会
    * S->S事件也会被onTransition捕捉（只要有定义）。
    */
  onTransition {
    case Active -> Idle =>
      stateData match {
        case Todo(ref, queue) => ref ! Batch(queue)
        case _ =>
      }
  }

  initialize()
}


object Buncher {

  /**
    * Buncher Actor可以接收的消息类型
    */
  sealed trait BuncherMessage
  final case class SetTarget(ref: ActorRef) extends BuncherMessage
  final case class Queue(obj: Any) extends BuncherMessage
  case object Flush extends BuncherMessage

  /**
    * Buncher Actor发送的消息类型
    */
  final case class Batch(obj: Seq[Any])

  /**
    * Buncher Actor可以处于的状态类型
    */
  sealed trait State
  case object Idle extends State
  case object Active extends State

  /**
    * Buncher Actor可以拥有的状态数据类型
    */
  sealed trait Data
  case object Uninitialized extends Data
  final case class Todo(target: ActorRef, queue: Seq[Any]) extends Data

}
