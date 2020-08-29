package testdriven

import akka.actor.{ Props, ActorRef, Actor }

object SendingActor {
  def props(receiver: ActorRef): Props = //receiver is passed through the Props to the constructor of the SendingActor
    Props(new SendingActor(receiver))
  case class Event(id: Long)
  //SortEvents and SortedEvents both use an immutable Vector
  case class SortEvents(unsorted: Vector[Event]) //The SortedEvent message is sent to the SendingActor
  case class SortedEvents(sorted: Vector[Event]) //The SortedEvent message is sent to the receiver
}

class SendingActor(receiver: ActorRef) extends Actor {
  import SendingActor._
  def receive: Receive = {
    case SortEvents(unsorted) =>
      receiver ! SortedEvents(unsorted.sortBy(_.id))
  }
}