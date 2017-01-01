package akkaDocuments.fsm.sampleInActivator

import akka.actor.{Actor, ActorRef, FSM}

/**
  * Created by fybai on 01/01/2017.
  */

/**
  * FSM特质直接继承自Actor，当扩展FSM特质时已经自动创建了一个Actor
  *
  * FSM特质定义了一个receive方法，该方法处理所有的内部消息，然后根据当前状态将其传递给FSM逻辑
  * 当覆盖receive方法时，需要记住状态的超时处理是通过传递消息给FSM逻辑来实现的 --> 源码中可以看到
  *
  * FSM特质携带两个类型参数：
  *   1. 所有状态的超类型，通常使用一些case objects扩展sealed trait来实现
  *   2. 由FSM模块自己维护的状态数据类型 -> 这是由FSM模块自己维护的
  *
  * 状态与状态数据构成了整个状态机的内部状态。
  *
  * 使用when(<name>[, stateTimeout = <timeout>])(stateFunction)方法来注册状态
  * 其中name是状态名，stateTimeout表示该状态的超时时间，stateFunction是一个PartialFunction[Event, State]
  *
  * 其中Event(msg: Any, data: D)中的D是FSM中的第二个类型参数
  * stateFunction的返回结果必须是下一个状态（或者终止该状态机），不然状态机这个概念就傻逼了。。
  * 下一个状态可能是当前状态，使用stay来描述；也可能是一个不同的状态，使用goto(state)
  * 同时结果状态可以使用forMax，using，replying等方法来进一步限制
  *
  *
  * onTransition(handler)该方法专注于转变；
  * 用该方法注册的handler是累积的，意味着你可以在某个Actor里面分散开来实现onTransition，还意味着所有的匹配的转变都会被调用，不止第一个
  *
  */
class Chopstick extends FSM[Chopstick.ChopstickState, Chopstick.TakenBy] {
  import Chopstick._
  import context._

  startWith(Available, TakenBy(system.deadLetters))

  when (Available) {
    case Event(Take, _) =>
      goto(Taken) using TakenBy(sender()) replying Taken(self)
  }

  when(Taken) {
    case Event(Take, _) =>
      stay replying Busy(self)
    case Event(Put, TakenBy(hakker)) if sender() == hakker =>
      goto(Available) using TakenBy(system.deadLetters)
  }

  initialize()
}


object Chopstick {

  /**
    * 筷子Actor可以接收到的消息
    */
  sealed trait ChopstickMessage
  object Take extends ChopstickMessage
  object Put extends ChopstickMessage
  final case class Taken(chopstick: ActorRef) extends ChopstickMessage
  final case class Busy(chopstick: ActorRef) extends ChopstickMessage

  /**
    * 筷子可能所处的状态
    */
  sealed trait ChopstickState
  case object Available extends ChopstickState
  case object Taken extends ChopstickState

  /**
    * 筷子Actor中包含的数据
    */
  final case class TakenBy(hakker: ActorRef)
}
