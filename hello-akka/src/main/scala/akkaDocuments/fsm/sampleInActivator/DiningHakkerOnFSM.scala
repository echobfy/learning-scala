package akkaDocuments.fsm.sampleInActivator

import akka.actor.{ActorSystem, Props}
import akkaDocuments.fsm.sampleInActivator.Hakker.Think

/**
  * Created by fybai on 01/01/2017.
  * Akka adaptation of
  * http://www.dalnefre.com/wp/2010/08/dining-philosophers-in-humus/
  */
object DiningHakkerOnFSM {

  val system = ActorSystem()

  def run(): Unit = {
    val chopsticks = for (i <- 1 to 5) yield system.actorOf(Props[Chopstick], "Chopstick" + i)

    val hakkers = for {
      (name, i) <- List("Ghosh", "Boner", "Klang", "Krasser", "Manie").zipWithIndex
    } yield system.actorOf(Props(classOf[Hakker], name, chopsticks(i), chopsticks((i + 1) % 5)))

    hakkers.foreach(_ ! Think)
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}
