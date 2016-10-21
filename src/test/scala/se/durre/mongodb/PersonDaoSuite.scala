package se.durre.mongodb

import java.time.LocalDate
import java.util.UUID

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
}
