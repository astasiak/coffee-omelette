package coffee

import akka.actor._
import spray.routing.SimpleRoutingApp
import spray.json.DefaultJsonProtocol
import java.util.concurrent.atomic.AtomicLong
import util.Properties
import spray.routing.HttpService
import akka.io.IO
import spray.can.Http

case class Flashcard(question: String, answer: String, id: Option[Long] = None)

object JsonImplicits extends DefaultJsonProtocol {
  implicit val impFlashcard = jsonFormat3(Flashcard)
}

object Server extends App with SimpleRoutingApp {
  
  implicit val system = ActorSystem("on-spray-can")

  val service = system.actorOf(Props[MyServiceActor], "demo-service")

  val portNumber = Properties.envOrElse("PORT", "8080").toInt
  
  IO(Http) ! Http.Bind(service, "0.0.0.0", portNumber)
  
}
class MyServiceActor extends Actor with MyService {
  def actorRefFactory = context
  
  def receive = runRoute(myRoute)
}

trait MyService extends HttpService {
  
  import spray.httpx.SprayJsonSupport._
  import JsonImplicits._

  val cardStorage: CardStorage = MemoryCardStorage
  //val cardStorage: CardStorage = new MongoCardStorage()
  
  val restRoute = get {
    path("cards") {
      complete(cardStorage.listCards())
    } ~
    path("cards" / IntNumber) { index =>
      complete(cardStorage.getCard(index))
    }
  } ~
  post {
    path("cards") {
      entity(as[Flashcard]) { card =>
        complete(cardStorage.addCard(card))
      }
    }
  } ~
  delete {
    path("cards" / IntNumber) { index =>
      cardStorage.deleteCard(index)
      complete(204, "Card deleted")
    }
  }
  
  val htmlRoute = get {
    path("") {
      getFromResource("static/index.html")
    } ~
    path("static" / Rest) { path=>
      getFromResource("static/%s" format path)
    }
  }
  
  val myRoute = restRoute ~ htmlRoute
}
