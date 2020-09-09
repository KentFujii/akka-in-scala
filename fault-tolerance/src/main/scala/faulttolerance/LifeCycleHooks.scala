package faulttolerance

import faulttolerance.LifeCycleHooks.{ForceRestart, ForceRestartException}
import akka.actor._

object LifeCycleHooks {
  object SampleMessage
  object ForceRestart
  private class ForceRestartException extends IllegalArgumentException("force restart")

}

class LifeCycleHooks extends Actor with ActorLogging {
  log.info("Constructor")

  override def preStart(): Unit = {
    log.info("preStart")
  }

  override def postStop(): Unit = {
    log.info("postStop")
  }

  //Exception thrown by the actor
  //When error occurred within the receive function, this is the message the actor was trying to process
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"preRestart. Reason: $reason when handling message: $message")
    super.preRestart(reason, message) //Warning: call the super implementation
  }

  //Exception thrown by the actor
  override def postRestart(reason: Throwable): Unit = {
    log.info("postRestart")
    super.postRestart(reason) //Warning: call the super implementation
  }

  def receive: Receive = {
    case ForceRestart =>
      throw new ForceRestartException
    case msg: AnyRef =>
      log.info(s"Received: '$msg'. Sending back")
      sender() ! msg
  }
}