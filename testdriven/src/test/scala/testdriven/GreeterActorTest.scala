package testdriven

import akka.testkit.{ CallingThreadDispatcher, EventFilter, TestKit }
import akka.actor.Props
import org.scalatest.WordSpecLike
import GreeterActor._

class GreeterActorTest extends TestKit(testSystem) //Uses the testSystem from the GreeterTest object
  with WordSpecLike
  with StopSystemAfterAll {

  "The Greeter" must {
    "say Hello World! when a Greeting(\"World\") is sent to it" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[GreeterActor].withDispatcher(dispatcherId) //Single-threaded environment
      val greeter = system.actorOf(props)
      EventFilter.info(message = "Hello World!", occurrences = 1).intercept { //Intercepts the log messages that were logged
        greeter ! Greeting("World")
      }
    }
  }
}

