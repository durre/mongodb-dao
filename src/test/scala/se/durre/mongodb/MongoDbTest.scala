package se.durre.mongodb

import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite, Matchers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

abstract class MongoDbTest(dbConfig: String = "mongodb") extends FunSuite with BeforeAndAfter with BeforeAndAfterAll with ScalaFutures with Matchers {

  def touchedCollections: Set[String]

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))
  protected val db: DefaultDB = MongoContext.connect(ConfigFactory.load().getConfig(dbConfig)).futureValue

  // Remove old documents before and after each test
  before {
    Await.result(emptyCollections, 5.seconds)
  }

  after {
    Await.result(emptyCollections, 5.seconds)
  }

  // Drop collections after each test suite to ensure no old indexes is laying around
  override def beforeAll: Unit = {
    Await.result(dropCollections, 5.seconds)
  }

  override def afterAll {
    Await.result(dropCollections, 5.seconds)
  }

  private def dbCollections: List[BSONCollection] = touchedCollections.toList.map(db[BSONCollection](_))

  private def emptyCollections: Future[Unit] =
    Future.sequence(dbCollections.map(_.remove(BSONDocument.empty))).map(_ => ())

  private def dropCollections: Future[Unit] =
    Future.sequence(dbCollections.map(_.drop(failIfNotFound = false))).map(_ => ())

}
