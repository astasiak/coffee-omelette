package coffee

import com.mongodb.casbah.Imports._

class MongoCardStorage extends CardStorage {
  val db = MongoClient()("test")
  val cardCollection = db("flashcards")
  val counterCollection = db("counters")
  
  private def getNexId() = {
    val result = counterCollection.findAndModify(
        MongoDBObject("_id"->"flashcountCounter"), MongoDBObject(), MongoDBObject(), false,
        $inc("seq"->1l), true, true)
    result.flatMap(_.getAs[Long]("seq"))
  }
  
  override def getCard(id: Long) = {
    val obj = cardCollection.findOne(MongoDBObject("id"->id))
    obj.flatMap(FlashcardMongoMapper.mapFromMongo(_))
  }
  override def addCard(card: Flashcard) = {
    val newCard = card.copy(id=getNexId())
    val obj = FlashcardMongoMapper.mapToMongo(newCard)
    cardCollection += obj
    newCard
  }
  override def listCards(): List[Flashcard] = {
    val cursor = cardCollection.find()
    FlashcardMongoMapper.mapFromMongo(cursor).toList
  }
  override def deleteCard(id: Long): Unit = {
    cardCollection -= MongoDBObject("id"->id)
  }
}

trait MongoMapper[T] {
  def mapToMongo(entity: T): MongoDBObject
  def mapFromMongo(obj: MongoDBObject): Option[T]
  def mapFromMongo(cursor: MongoCursor): Iterator[T] = {
    for { x <- cursor;t = mapFromMongo(x) if t!=None }
      yield t.get
  }
}

object FlashcardMongoMapper extends MongoMapper[Flashcard] {
  override def mapToMongo(entity: Flashcard): MongoDBObject = { 
    MongoDBObject(
        "id"->entity.id,
        "question"->entity.question,
        "answer"->entity.answer)
  }
  override def mapFromMongo(obj: MongoDBObject): Option[Flashcard] = {
    val question = obj.getAs[String]("question")
    val answer = obj.getAs[String]("answer")
    val id = obj.getAs[Long]("id")
    (question, answer) match {
      case (Some(q),Some(a)) => Some(Flashcard(q,a,id))
      case _ => None
    }
  }
}