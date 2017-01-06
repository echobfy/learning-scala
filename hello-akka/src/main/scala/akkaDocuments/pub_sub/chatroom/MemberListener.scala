package akkaDocuments.pub_sub.chatroom

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, Address}
import akka.cluster.{Cluster, MemberStatus}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberEvent, MemberRemoved, MemberUp}

/**
  * Created by fybai on 06/01/2017.
  */
class MemberListener extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberEvent])
  }

  override def postStop(): Unit = {
    cluster unsubscribe self
  }

  var nodes = Set.empty[Address]

  override def receive: Receive = {
    case state: CurrentClusterState =>
      nodes = state.members.collect {
        case m if m.status == MemberStatus.Up => m.address
      }
    case MemberUp(member) =>
      nodes += member.address
      log.info("Member is Up: {}. {} nodes in cluster", member.address, nodes.size)
    case MemberRemoved(member, _) =>
      nodes -= member.address
      log.info("Member is Removed: {}. {} nodes in cluster", member.address, nodes.size)
    case _: MemberEvent =>
      // ignore
  }
}
