package se.durre.mongodb.formats

import java.time.{Instant, ZoneId, ZonedDateTime, LocalDateTime}

import org.scalatest._
import reactivemongo.bson.BSONDateTime

class CommonDbFormatsSpec extends WordSpec with Matchers {

  "CommonDbFormats" should {

    "be able to convert LocalDateTime to BSONDateTime" in {
      val when = LocalDateTime.of(2016, 1, 16, 22, 0)
      val expectedBson = BSONDateTime(ZonedDateTime.of(when, ZoneId.of("UTC")).toInstant.toEpochMilli)
      val bson = CommonDbFormats.LocalDateTimeHandler.write(when)
      bson shouldBe expectedBson
    }

    "be able to convert BSONDateTime to LocalDateTime" in {
      val thisInstant = System.currentTimeMillis()
      val expectedDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(thisInstant), ZoneId.of("UTC"))

      val dateTime = CommonDbFormats.LocalDateTimeHandler.read(BSONDateTime(thisInstant))
      dateTime shouldBe expectedDateTime
    }
  }
}
