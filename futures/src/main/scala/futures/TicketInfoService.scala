package futures

import scala.concurrent.Future
import scala.util.control.NonFatal
import scala.concurrent.ExecutionContext.Implicits.global

//what about timeout? or at least termination condition?
// future -> actors scheduling time
trait TicketInfoService extends WebServiceCalls {
  type Recovery[T] = PartialFunction[Throwable,T]

  // recover with None
  def withNone[T]: Recovery[Option[T]] = { case NonFatal(e) => None }

  // recover with empty sequence
  def withEmptySeq[T]: Recovery[Seq[T]] = { case NonFatal(e) => Seq() }

  // recover with the ticketInfo that was built in the previous step
  def withPrevious(previous: TicketInfo): Recovery[TicketInfo] = {
    case NonFatal(_) => previous
  }

  def getTicketInfo(ticketNr: String, location: Location): Future[TicketInfo] = {
    val emptyTicketInfo = TicketInfo(ticketNr, location)
    val eventInfo = getEvent(ticketNr, location).recover(withPrevious(emptyTicketInfo))

    eventInfo.flatMap { info =>

      val infoWithWeather = getWeather(info)

      val infoWithTravelAdvice = info.event.map { event =>
        getTravelAdvice(info, event)
      }.getOrElse(eventInfo)


      val suggestedEvents = info.event.map { event =>
        getSuggestions(event)
      }.getOrElse(Future.successful(Seq()))

      val ticketInfos = Seq(infoWithTravelAdvice, infoWithWeather)

      val infoWithTravelAndWeather: Future[TicketInfo] = Future.fold(ticketInfos)(info) { (acc, elem) =>
        val (travelAdvice, weather) = (elem.travelAdvice, elem.weather)

        acc.copy(travelAdvice = travelAdvice.orElse(acc.travelAdvice),
                  weather = weather.orElse(acc.weather))
      }

      for(info <- infoWithTravelAndWeather;
        suggestions <- suggestedEvents
      ) yield info.copy(suggestions = suggestions)
    }
  }

  def getTravelAdvice(info: TicketInfo, event: Event): Future[TicketInfo] = {
    val futureRoute = callTrafficService(info.userLocation, event.location, event.time).recover(withNone)
    val futurePublicTransport = callPublicTransportService(info.userLocation, event.location, event.time).recover(withNone)

    futureRoute.zip(futurePublicTransport).map { case(routeByCar, publicTransportAdvice) =>
      val travelAdvice = TravelAdvice(routeByCar, publicTransportAdvice)
      info.copy(travelAdvice = Some(travelAdvice))
    }
  }

  def getWeather(ticketInfo: TicketInfo): Future[TicketInfo] = {
    val futureWeatherX = callWeatherXService(ticketInfo).recover(withNone)
    val futureWeatherY = callWeatherYService(ticketInfo).recover(withNone)
    Future.firstCompletedOf(Seq(futureWeatherX, futureWeatherY)).map { weatherResponse =>
      ticketInfo.copy(weather = weatherResponse)
    }
  }

  def getPlannedEvents(event: Event, artists: Seq[Artist]): Future[Seq[Event]] = {
    val events = artists.map(artist=> callArtistCalendarService(artist, event.location))
    Future.sequence(events)
  }

  def getSuggestions(event: Event): Future[Seq[Event]] = {
    val futureArtists = callSimilarArtistsService(event).recover(withEmptySeq)
    for(artists <- futureArtists.recover(withEmptySeq);
        events <- getPlannedEvents(event, artists).recover(withEmptySeq)
    ) yield events
  }
}
