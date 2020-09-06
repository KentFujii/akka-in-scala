package testdriven

import akka.actor.Actor

class EchoActor extends Actor {
  def receive: Receive = {
    case msg =>
      sender() ! msg //Whatever is received is simply sent bach to (implicit) sender
  }
}
