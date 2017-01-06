package akkaDocuments.pub_sub.chatroom

import java.util.concurrent.ThreadLocalRandom

import scala.concurrent.duration._
import akka.actor.Actor
import akka.actor.Actor.Receive

/**
  * Created by fybai on 06/01/2017.
  */

object RandomUser {
  case object Tick
  val phrases = Vector(
    "Creativity is allowing yourself to make mistakes. Art is knowing which ones to keep.",
    "The best way to compile inaccurate information that no one wants is to make it up.",
    "Decisions are made by people who have time, not people who have talent.",
    "Frankly, I'm suspicious of anyone who has a strong opinion on a complicated issue.",
    "Nothing inspires forgiveness quite like revenge.",
    "Free will is an illusion. People always choose the perceived path of greatest pleasure.",
    "The best things in life are silly.",
    "Remind people that profit is the difference between revenue and expense. This makes you look smart.",
    "Engineers like to solve problems. If there are no problems handily available, they will create their own problems."
  )
}


class RandomUser extends Actor {
  import RandomUser._
  import context.dispatcher

  val client = context.actorOf(ChatClient.props(self.path.name), "client")

  def scheduler = context.system.scheduler
  def rnd = ThreadLocalRandom.current()

  override def preStart(): Unit = {
    scheduler.scheduleOnce(5.seconds, self, Tick)
  }

  override def postRestart(reason: Throwable): Unit = ()

  override def receive: Receive = {
    case Tick =>
      scheduler.scheduleOnce(rnd.nextInt(5, 20).seconds, self, Tick)
      val msg = phrases(rnd.nextInt(phrases.size))
      client ! ChatClient.Publish(msg)
  }
}
