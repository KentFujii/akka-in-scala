package testdriven

import scala.util.Random
import akka.testkit.TestKit
import akka.actor.ActorSystem
import org.scalatest.{ WordSpecLike, MustMatchers }
import SendingActor._

class SendingActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {
  "A Sending Actor" must {
    "send a message to another actor when it has finished processing" in {
      val props = SendingActor.props(testActor) //Receiver is passed to props method that creates Props
      val sendingActor = system.actorOf(props, "sendingActor")

      val size = 1000
      val maxInclusive = 100000

      def randomEvents(): Vector[Event] = (0 until size).map{ _ =>
        Event(Random.nextInt(maxInclusive))
      }.toVector

      val unsorted = randomEvents() //Randomized unsorted list of events is created
      val sortEvents = SortEvents(unsorted)
      sendingActor ! sortEvents

      expectMsgPF() {
        case SortedEvents(events) => //testActor should receive a sorted Vector of Events
          events.size must be(size)
          unsorted.sortBy(_.id) must be(events)
      }
    }
  }
}