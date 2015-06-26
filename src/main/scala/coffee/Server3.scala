package coffee

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import spray.json.DefaultJsonProtocol
import java.util.concurrent.atomic.AtomicLong
import util.Properties

object Server3 extends /*App with */SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()
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
  
  val route = restRoute ~ htmlRoute

  val portNumber = Properties.envOrElse("PORT", "8080").toInt
  
  startServer(interface = "localhost", port = portNumber)(route)
}
