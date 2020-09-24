package futures

import org.joda.time.{Duration, DateTime}

//TicketInfo case class collects travel advice, weather, and event suggestions
case class TicketInfo(ticketNr: String,
                      userLocation: Location,
                      event: Option[Event]=None,
                      travelAdvice: Option[TravelAdvice]=None,
                      weather: Option[Weather]=None,
                      suggestions: Seq[Event]=Seq())

case class Event(name: String,location: Location, time: DateTime)

case class Artist(name: String, calendarUri: String)

case class Location(lat: Double, lon: Double)

//To keep example simple, the route is just a string
case class RouteByCar(route: String,
                      timeToLeave: DateTime,
                      origin: Location,
                      destination: Location,
                      estimatedDuration: Duration,
                      trafficJamTime: Duration)

//To keep example simple, the advice is just a string
case class PublicTransportAdvice(advice: String,
                                 timeToLeave: DateTime,
                                 origin: Location,
                                 destination: Location,
                                 estimatedDuration: Duration)

case class TravelAdvice(routeByCar: Option[RouteByCar]=None,
                        publicTransportAdvice: Option[PublicTransportAdvice]=None)

case class Weather(temperature: Int, precipitation: Boolean)