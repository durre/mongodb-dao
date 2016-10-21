package se.durre.mongodb

import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import reactivemongo.api.collections.bson.BSONCollection
import scala.concurrent.duration._

import scala.concurrent.{Future, Await}

abstract class MongoDbTest(dbConfig: String = "mongodb") extends FunSuite with BeforeAndAfter with ScalaFutures with Matchers {

  def touchedCollections: Set[String]

  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))
  protected val db = MongoContext.connect(ConfigFactory.load().getConfig(dbConfig))

  before {
    Await.result(cleanup, 5.seconds)
  }

  after {
    Await.result(cleanup, 5.seconds)
  }

  private def dbCollections: List[BSONCollection] = touchedCollections.toList.map(db[BSONCollection](_))

  private def cleanup: Future[Unit] = {
    val futureDropCollections = Future.sequence(dbCollections.map(_.drop()))
    futureDropCollections
      .map(_ => ())
      .recover {
        case _ => ()
      }
  }
}
