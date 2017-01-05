package se.durre.mongodb

import java.time.LocalDate
import java.util.UUID

import reactivemongo.bson.BSONDocument

class PersonDaoSuite extends MongoDbTest {

  val dao = new PersonDao(db)

  // These collections will be dropped before & after each test
  override def touchedCollections: Set[String] = Set(dao.collectionName)

  val person = Person(
    id = UUID.randomUUID(),
    name = "Bob",
    born = LocalDate.of(1979, 12, 19),
    graduated = LocalDate.of(1999, 6, 10)
  )

  test("insert a document") {
    dao.insert(person).futureValue
    val found = dao.findById(person.id).futureValue
    found shouldBe Some(person)
  }

  test("update a document") {
    dao.insert(person).futureValue
    val improvedPerson = person.copy(
      name = "Bobby",
      born = LocalDate.of(1980, 12, 19),
      graduated = LocalDate.of(2000, 12, 19)
    )

    dao.updateById(person.id, improvedPerson)
    val found = dao.findById(person.id).futureValue
    found shouldBe Some(improvedPerson)
  }

  test("find all documents") {
    val anotherPerson = person.copy(id = UUID.randomUUID(), born = LocalDate.of(1999, 12, 19))
    dao.insert(person).futureValue
    dao.insert(anotherPerson).futureValue

    val found = dao.findAll(sort = BSONDocument("born" -> -1)).futureValue
    found shouldBe List(anotherPerson, person)
  }

  test("remove by id") {
    dao.insert(person).futureValue
    dao.findById(person.id).futureValue shouldBe Some(person)

    dao.removeById(person.id).futureValue
    dao.findById(person.id).futureValue shouldBe None
  }
}
