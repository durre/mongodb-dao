# mongodb-dao

Contains some common functionality when working with MongoDb together with [Reactivemongo](http://reactivemongo.org/)

## Usage

```
libraryDependencies ++= Seq(
  "com.github.durre" %% "mongodb-dao" % "1.2.0",
  // This is a bit akward due to a bug in sbt I believe
  ("com.github.durre" %% "mongodb-dao" % "1.2.0" % "test").classifier("tests")
)
```

### The dao
```scala
import se.durre.mongodb.formats.CommonDbFormats._

case class Person(
  id: UUID,
  name: String
)

class PersonDao(db: DefaultDB) extends MongoDao[Person, UUID](db, collectionName = "persons") {

  override protected implicit def reader: BSONDocumentReader[Person] = new BSONDocumentReader[Person] {
    override def read(bson: BSONDocument): Person = {
      (for {
        id <- bson.getAs[UUID]("_id")
        name <- bson.getAs[String]("name")
      } yield Person(id, name)).get
    }
  }

  override protected implicit def writer: BSONDocumentWriter[Person] = new BSONDocumentWriter[Person] {
    override def write(t: Person): BSONDocument = BSONDocument(
      "_id" -> t.id,
      "name" -> t.name
    )
  }
}
```


### Configuration

In your application.conf

```
mongodb {
  uri = "mongodb://localhost:27017/testdb"
}
```

### Create a connection

```scala
val config = ConfigFactory.load() 
MongoContext.connect(config.getConfig("mongodb")).map { db =>
  val dao = new PersonDao(db)
}
```


### Writing tests

You can use the supplied helper class, **MongoDbTest**.

```scala
class PersonDaoSuite extends MongoDbTest {

  val dao = new PersonDao(db)

  // These collections will be dropped before & after each test
  override def touchedCollections: Set[String] = Set(dao.collectionName)

  test("insert a document") {
  
    val person = Person(
      id = UUID.randomUUID(),
      name = "Bob"
    )
  
    dao.insert(person).futureValue    
    val found = dao.findById(person.id).futureValue
    found shouldBe Some(person)
  }
}
```
