package upandrunning

import akka.actor._
import akka.util.Timeout

import scala.concurrent.Future

object BoxOffice {
  def props(implicit timeout: Timeout): Props = Props(new BoxOffice)
  def name = "boxOffice"

  case class CreateEvent(name: String, tickets: Int) // Message to create an event
  case class GetEvent(name: String) // Message to get an event
  case object GetEvents // Message to request all events
  case class GetTickets(event: String, tickets: Int) // Message to get tickets for an event
  case class CancelEvent(name: String) // Message to cancel the event

  case class Event(name: String, tickets: Int) // Message describing the event
  case class Events(events: Vector[Event]) // Message to describe a list of events

  sealed trait EventResponse // Message response to CreateEvent
  case class EventCreated(event: Event) extends EventResponse // Message to indicate the event was created
  case object EventExists extends EventResponse // Message to indicate that the event already exists

}

class BoxOffice(implicit timeout: Timeout) extends Actor {
  import BoxOffice._
  import context._


  def createTicketSeller(name: String): ActorRef =
    context.actorOf(TicketSeller.props(name), name)

  def receive: Receive = {
    case CreateEvent(name, tickets) =>
      def create(): Unit = {
        val eventTickets = createTicketSeller(name)
        val newTickets = (1 to tickets).map { ticketId =>
          TicketSeller.Ticket(ticketId)
        }.toVector
        eventTickets ! TicketSeller.Add(newTickets)
        sender() ! EventCreated(Event(name, tickets))
      }
      context.child(name).fold(create())(_ => sender() ! EventExists)



    case GetTickets(event, tickets) =>
      def notFound(): Unit = sender() ! TicketSeller.Tickets(event)
      def buy(child: ActorRef): Unit =
        child.forward(TicketSeller.Buy(tickets))

      context.child(event).fold(notFound())(buy)


    case GetEvent(event) =>
      def notFound(): Unit = sender() ! None
      def getEvent(child: ActorRef): Unit = child forward TicketSeller.GetEvent
      context.child(event).fold(notFound())(getEvent)


    case GetEvents =>
      import akka.pattern.{ask, pipe}

      def getEvents = context.children.map { child =>
        self.ask(GetEvent(child.path.name)).mapTo[Option[Event]]
      }
      def convertToEvents(f: Future[Iterable[Option[Event]]]) =
        f.map(_.flatten).map(l=> Events(l.toVector))

      pipe(convertToEvents(Future.sequence(getEvents))) to sender()


    case CancelEvent(event) =>
      def notFound(): Unit = sender() ! None
      def cancelEvent(child: ActorRef): Unit = child forward TicketSeller.Cancel
      context.child(event).fold(notFound())(cancelEvent)
  }
}
