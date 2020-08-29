package testdriven

import akka.actor.{ Actor, Props, ActorRef }

object FilteringActor {
  def props(nextActor: ActorRef, bufferSize: Int): Props =
    Props(new FilteringActor(nextActor, bufferSize))
  case class Event(id: Long)
}

//Max size for the buffer is passed into constructor
class FilteringActor(nextActor: ActorRef, bufferSize: Int) extends Actor {
  import FilteringActor._
  var lastMessages: Vector[Event] = Vector[Event]()
  def receive: Receive = {
    case msg: Event =>
      if (!lastMessages.contains(msg)) {
        lastMessages = lastMessages :+ msg
        //Event is sent to next actor if it's not found in the buffer
        nextActor ! msg
        if (lastMessages.size > bufferSize) {
          // discard the oldest
          //Oldest event in the buffer is discarded when max buffer size is reached
          lastMessages = lastMessages.tail
        }
      }
  }
}