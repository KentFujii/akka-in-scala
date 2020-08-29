package testdriven

import akka.testkit.TestKit
import akka.actor.{ Actor, Props, ActorRef, ActorSystem }
import org.scalatest.{MustMatchers, WordSpecLike }

class FilteringActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {
  "A Filtering Actor" must {

    "filter out particular messages" in {
      import FilteringActor._
      val props = FilteringActor.props(testActor, 5)
      val filter = system.actorOf(props, "filter-1")
      //Sends a couple of events including duplicates
      filter ! Event(1)
      filter ! Event(2)
      filter ! Event(1)
      filter ! Event(3)
      filter ! Event(1)
      filter ! Event(4)
      filter ! Event(5)
      filter ! Event(5)
      filter ! Event(6)
      //Receives messages until the case statement doesn't match anymore
      val eventIds = receiveWhile() {
        case Event(id) if id <= 5 => id
      }
      //Asserts that the duplicates aren't in the result
      eventIds must be(List(1, 2, 3, 4, 5))
      expectMsg(Event(6))
    }


    "filter out particular messages using expectNoMsg" in {
      import FilteringActor._
      val props = FilteringActor.props(testActor, 5)
      val filter = system.actorOf(props, "filter-2")
      filter ! Event(1)
      filter ! Event(2)
      expectMsg(Event(1))
      expectMsg(Event(2))
      filter ! Event(1)
      expectNoMsg
      filter ! Event(3)
      expectMsg(Event(3))
      filter ! Event(1)
      expectNoMsg
      filter ! Event(4)
      filter ! Event(5)
      filter ! Event(5)
      expectMsg(Event(4))
      expectMsg(Event(5))
      expectNoMsg()
    }

  }
}

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