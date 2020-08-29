package testdriven

import scala.util.Random
import akka.testkit.TestKit
import akka.actor.{ Props, ActorRef, Actor, ActorSystem }
import org.scalatest.{WordSpecLike, MustMatchers}

class SendingActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {

  "A Sending Actor" must {
    "send a message to another actor when it has finished processing" in {
      import SendingActor._
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