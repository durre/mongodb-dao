package se.durre.mongodb

import java.time.LocalDate
import java.util.UUID

import reactivemongo.api.DefaultDB
import reactivemongo.bson.{BSONDocumentWriter, BSONDocument, BSONDocumentReader}
import se.durre.mongodb.formats.CommonDbFormats._
import se.durre.mongodb.formats.CommonDbFormats


case class Person(
  id: UUID,
  name: String,
  born: LocalDate,
  graduated: LocalDate
)

class PersonDao(db: DefaultDB) extends MongoDao[Person, UUID](db, collectionName = "persons") {

  override protected implicit def reader: BSONDocumentReader[Person] = new BSONDocumentReader[Person] {
    override def read(bson: BSONDocument): Person = {
      (for {
        id <- bson.getAs[UUID]("_id")
        name <- bson.getAs[String]("name")
        born <- bson.getAs[LocalDate]("born")(CommonDbFormats.LocalDateAsStringHandler)
        graduated <- bson.getAs[LocalDate]("graduated")(CommonDbFormats.LocalDateAsDateHandler)
      } yield Person(id, name, born, graduated)).get
    }
  }

  override protected implicit def writer: BSONDocumentWriter[Person] = new BSONDocumentWriter[Person] {
    override def write(t: Person): BSONDocument = BSONDocument(
      "_id" -> t.id,
      "name" -> t.name,
      "born" -> CommonDbFormats.LocalDateAsStringHandler.write(t.born),
      "graduated" -> CommonDbFormats.LocalDateAsDateHandler.write(t.graduated)
    )
  }
}
