package se.durre.mongodb

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.DefaultDB
import reactivemongo.bson._

import scala.concurrent.{ExecutionContext, Future}

abstract class MongoDao[T, ID](db: DefaultDB, val collectionName: String)(implicit idWriter: BSONWriter[ID, _ <: BSONValue], idReader: BSONReader[_ <: BSONValue, ID]) {

  protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  protected lazy val collection: BSONCollection = db.collection(collectionName)
  protected implicit def reader: BSONDocumentReader[T]
  protected implicit def writer: BSONDocumentWriter[T]

  protected def improvedStacktrace[A](method: => String): PartialFunction[Throwable, A] = {
    case e: Throwable => throw new RuntimeException(s"Exception in DAO $getClass.$method. ${e.getMessage}", e)
  }

  def insert(obj: T): Future[T] = collection
    .insert(obj)
    .map(_ => obj)
    .recover(improvedStacktrace(s"insert($obj)"))


  def findOne(query: BSONDocument): Future[Option[T]] = collection
    .find(query)
    .one[T]
    .recover(improvedStacktrace(s"findOne(${BSONDocument.pretty(query)})"))

  def findById(id: ID): Future[Option[T]] =
    findOne(BSONDocument("_id" -> id))

}
