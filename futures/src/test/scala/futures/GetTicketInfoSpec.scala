package futures

import org.scalatest.MustMatchers
import org.scalatest.WordSpec
import scala.concurrent.Await
import scala.concurrent.duration._


class GetTicketInfoSpec extends WordSpec with MustMatchers {
  "getTicketInfo" must {
    "return a complete ticket info when all futures are successful" in {
      val ticketInfo = Await.result(MockTicketInfoServiceImpl.getTicketInfo("1234", Location(1d,2d)), 10.seconds)

      ticketInfo.event.isEmpty must be(false)
      ticketInfo.event.foreach( event=> event.name must be("Quasimoto"))
      ticketInfo.travelAdvice.isEmpty must be(false)
      ticketInfo.suggestions.map(_.name) must be (Seq("Madlib", "OhNo", "Flying Lotus"))
    }
    "return an incomplete ticket info when getEvent fails" in {
      val ticketInfo = Await.result(MockTicketInfoServiceImpl.getTicketInfo("4321", Location(1d,2d)), 10.seconds)

      ticketInfo.event.isEmpty must be(true)
      ticketInfo.travelAdvice.isEmpty must be(true)
      ticketInfo.suggestions.isEmpty must be (true)
    }
  }
}
