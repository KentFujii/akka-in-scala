package testdriven

import org.scalatest.{WordSpecLike, MustMatchers}
import akka.testkit.TestKit
import akka.actor._

//The name of this actor system, used to distinguish multiple ones within the same JVM & class loader
//Extends from TestKit and provides an actor system for testing
class SilentActor01Test extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike //WordSpecLike provides easy-to-read DSL for testing in BDD style
  with MustMatchers //MustMatchers provides easy-to-read assertions
  with StopSystemAfterAll { //Makes sure the system is stopped after all tests

  "A Silent Actor" must {
    "change state when it receives a message, single threaded" ignore {
      //Write the test, first fail
      fail("not implemented yet")
    }
    "change state when it receives a message, multi-threaded" ignore {
      //Write the test, first fail
      fail("not implemented yet")
    }
  }
}

class SilentActor extends Actor {
  def receive: Receive = {
    case msg =>
  }
}
