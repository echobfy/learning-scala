package akkaDocuments.scheduler

import java.util.Date

import akka.actor.ActorSystem

/**
  * Created by fybai on 04/01/2017.
  */
object Main extends App {
  val system = ActorSystem("AkkaScalaActorSystem")
  val scheduleActor = system.actorOf(ScheduleActor.props, "ScheduleActor")

  1 to 1000 foreach { id => scheduleActor ! ScheduleActor.Schedule(id, new Date) }
  system.awaitTermination()
}
