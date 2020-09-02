package testdriven


import akka.actor.{ ActorLogging, Actor, ActorSystem }
import com.typesafe.config.ConfigFactory

object GreeterActor {
  case class Greeting(message: String)
  val testSystem: ActorSystem = {
    val config = ConfigFactory.parseString(
      """
         akka.loggers = [akka.testkit.TestEventListener]
      """)
    ActorSystem("testsystem", config)
  }
}


class GreeterActor extends Actor with ActorLogging {
  import GreeterActor._
  def receive: Receive = {
    case Greeting(message) => log.info("Hello {}!", message) //Prints the greeting it receives
  }
}
