package up_and_running

import com.typesafe.config.{Config, ConfigFactory}

object Main extends App {
  val config = ConfigFactory.load()
  println(config)
}