package akkaDocuments.persistence.exampleInDoc

import akka.actor._
import akka.persistence._

/**
  * Created by fybai on 01/01/2017.
  */

/**
  * Akka持久化可以使得有状态的actor持久化其内部状态，这样当某个actor启动，重启或者在集群迁移时能够重新恢复之前的状态
  *
  * Akka持久化背后的核心是只保存内部状态之间的改变而不是直接保存当前状态
  * Akka持久化提供了至少一次消息语义的点对点交互
  */

case class Cmd(data: String)    // 代表Command
case class Evt(data: String)    // 代表Event

case class ExampleState(events: List[String] = Nil) {
  def updated(evt: Evt): ExampleState = copy(evt.data :: events)
  def size: Int = events.length
  override def toString: String = events.reverse.toString
}

/**
  * ExamplePersistentActor的状态是一个已经持久化的event列表，包含在ExampleState中
  */
class ExamplePersistentActor extends PersistentActor {
  override def persistenceId = "sample-id-1"

  override def recovery: Recovery = super.recovery

  var state = ExampleState()

  def updateState(event: Evt): Unit =
    state = state.updated(event)

  def numEvents =
    state.size

  /**
    * 定义了在恢复过程中，通过处理Evt和SnapshotOffer消息，来更新状态
    */
  val receiveRecover: Receive = {
    case evt: Evt                                 => updateState(evt)
    case SnapshotOffer(_, snapshot: ExampleState) => state = snapshot
  }

  /**
    *
    * persist方法异步持久化events，成功持久化后会激活event handler来执行
    */
  val receiveCommand: Receive = {
    case Cmd(data) =>
      persist(Evt(s"${data}-${numEvents}"))(updateState)
      persist(Evt(s"${data}-${numEvents + 1}")) { event =>
        updateState(event)      // event handler的主要作用就是使用当前的event data来改变actor状态，然后通知其状态成功改变
        context.system.eventStream.publish(event)
      }
    case "snap"  => saveSnapshot(state)
    case "print" => println(state)
  }

}
