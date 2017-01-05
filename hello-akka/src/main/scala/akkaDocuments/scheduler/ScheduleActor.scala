package akkaDocuments.scheduler

import java.util.Date

import akka.actor.Actor.Receive
import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, Props}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by fybai on 04/01/2017.
  */
class ScheduleActor extends Actor with ActorLogging {
  import ScheduleActor._

  override def receive: Receive = {
    case Schedule(id, date) =>
      log.info("Received message for id: {} and on: {}", id, date)
      context.system.scheduler.scheduleOnce(5.seconds, self, Schedule(id, new Date))
  }
}

object ScheduleActor {
  val props = Props[ScheduleActor]
  case class Schedule(id: Long, time: Date)
}
