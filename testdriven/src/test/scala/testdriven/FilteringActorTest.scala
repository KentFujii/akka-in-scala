package testdriven


import akka.testkit.TestKit
import akka.actor.{ActorRef, ActorSystem}
import org.scalatest.{MustMatchers, WordSpecLike}
import FilteringActor._

class FilteringActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {

  "A Filtering Actor" must {
    "filter out particular messages" in {
      val props = FilteringActor.props(testActor, 5)
      val filter: ActorRef = system.actorOf(props, "filter-1")
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
