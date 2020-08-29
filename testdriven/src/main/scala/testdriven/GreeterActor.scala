package testdriven


import akka.actor.{ ActorLogging, Actor, ActorSystem }
import com.typesafe.config.ConfigFactory

case class Greeting(message: String)

object GreeterActor {
  val testSystem: ActorSystem = {
    val config = ConfigFactory.parseString(
      """
         akka.loggers = [akka.testkit.TestEventListener]
      """)
    ActorSystem("testsystem", config)
  }
}



class GreeterActor extends Actor with ActorLogging {
  def receive: Receive = {
    case Greeting(message) => log.info("Hello {}!", message)
  }
}
