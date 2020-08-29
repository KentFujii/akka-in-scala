package testdriven

import akka.testkit.{ CallingThreadDispatcher, EventFilter, TestKit }
import akka.actor.Props
import org.scalatest.WordSpecLike
import GreeterActor._

class GreeterActorTest extends TestKit(testSystem)
  with WordSpecLike
  with StopSystemAfterAll {

  "The Greeter" must {
    "say Hello World! when a Greeting(\"World\") is sent to it" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[GreeterActor].withDispatcher(dispatcherId)
      val greeter = system.actorOf(props)
      EventFilter.info(message = "Hello World!",
        occurrences = 1).intercept {
        greeter ! Greeting("World")
      }
    }
  }
}

