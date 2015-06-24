package coffee

import java.util.concurrent.atomic.AtomicLong
import scala.collection.mutable.Map

trait CardStorage {
  def getCard(id: Long): Option[Flashcard]
  def addCard(card: Flashcard): Flashcard
  def listCards(): List[Flashcard]
  def deleteCard(id: Long): Unit
}

object MemoryCardStorage extends CardStorage {
  val map = Map[Long,Flashcard]()
  var index = new AtomicLong(1)
  map.put(1, Flashcard("hello","ciao",None))
  map.put(2, Flashcard("hotel","l'albergo",Some(2)))
  
  override def getCard(id: Long) = map.get(id)
  override def addCard(card: Flashcard) = {
    val indexValue = index.getAndIncrement
    map.put(indexValue, card.copy(id=Some(indexValue)))
    card
  }
  override def listCards() = map.values.toList.sortBy(_.id)
  override def deleteCard(id: Long) = map.remove(id)
}