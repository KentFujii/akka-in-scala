package testdriven

import org.scalatest.{WordSpecLike, MustMatchers}
import akka.testkit.TestKit
import akka.actor._

//The name of this actor system, used to distinguish multiple ones within the same JVM & class loader
//Extends from TestKit and provides an actor system for testing
class SilentActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike //WordSpecLike provides easy-to-read DSL for testing in BDD style
  with MustMatchers //MustMatchers provides easy-to-read assertions
  with StopSystemAfterAll { //Makes sure the system is stopped after all tests

  "A Silent Actor" must {
    "change internal state when it receives a message, multi" in {
      import SilentActor._ //A companion object that keeps related messages together

      val silentActor = system.actorOf(Props[SilentActor], "s3") //Test system is used to create an actor
      silentActor ! SilentMessage("whisper1")
      silentActor ! SilentMessage("whisper2")
      silentActor ! GetState(testActor) //Message is added to the companion to get state
      expectMsg(Vector("whisper1", "whisper2")) //Used to check what message(s) have been sent to the testActor
    }
  }
}

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