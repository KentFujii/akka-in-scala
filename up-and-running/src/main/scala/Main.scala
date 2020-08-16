package up_and_running

import com.typesafe.config.{Config, ConfigFactory}
import akka.util.Timeout

object Main extends App with RequestTimeout {
  val config = ConfigFactory.load()
  println(config.getString("http.host"))
  println(config.getInt("http.port"))
}

trait RequestTimeout {
  import scala.concurrent.duration._
  def requestTimeout(config: Config): Timeout = {
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}