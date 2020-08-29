package testdriven

import akka.actor._

object SilentActor {
  case class SilentMessage(data: String)
  case class GetState(receiver: ActorRef) //GetState message is added for testing purposes
}

class SilentActor extends Actor {
  import SilentActor._
  var internalState: Seq[String] = Vector[String]()

  def receive: Receive = {
    case SilentMessage(data) =>
      internalState = internalState :+ data
    case GetState(receiver) => receiver ! internalState //Internal state is sent to ActorRef in GetState message
  }
}