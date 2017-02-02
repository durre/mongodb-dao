package com.github.durre.mongodb.formats

import java.io.{DataOutputStream, ByteArrayOutputStream}
import java.nio.ByteBuffer
import java.time._
import java.time.format.DateTimeFormatter
import java.util.UUID

import reactivemongo.bson.Subtype.UuidSubtype
import reactivemongo.bson._

object CommonDbFormats {

  private val utc = ZoneId.of("UTC")

  /**
    * Option number one, store them as strings. Ex: "2015-12-19"
    *
    * implicit val myDateHandler = CommonFormats.LocalDateAsStringHandler
    *
    */
  object LocalDateAsStringHandler extends BSONHandler[BSONString, LocalDate] {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override def write(t: LocalDate): BSONString = BSONString(formatter.format(t))
    override def read(bson: BSONString): LocalDate = LocalDate.parse(bson.as[String], formatter)
  }

  /**
    * Option number two, store them as ISODate.
    *
    * implicit val myDateHandler = CommonFormats.LocalDateAsDateHandler
    *
    */
  object LocalDateAsDateHandler extends BSONHandler[BSONDateTime, LocalDate] {

    override def write(t: LocalDate): BSONDateTime = BSONDateTime(t.atStartOfDay().atZone(utc).toInstant.toEpochMilli)
    override def read(bson: BSONDateTime): LocalDate = {
      val instant = Instant.ofEpochMilli(bson.value)
      LocalDateTime.ofInstant(instant, utc).toLocalDate
    }
  }


  /**
    * Handles storage of a LocalDateTime instance. Should represent the UTC time.
    */
  implicit object LocalDateTimeHandler extends BSONHandler[BSONDateTime, LocalDateTime] {
    override def write(t: LocalDateTime): BSONDateTime = BSONDateTime(ZonedDateTime.of(t, utc).toInstant.toEpochMilli)
    override def read(bson: BSONDateTime): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(bson.value), utc)
  }


  // Be wary of rounding errors when using this handler
  implicit object BigDecimalHandler extends BSONHandler[BSONDouble, BigDecimal] {
    def read(double: BSONDouble) = BigDecimal(double.value)
    def write(bd: BigDecimal) = BSONDouble(bd.toDouble)
  }

  implicit object UUIDHandler extends BSONHandler[BSONBinary, UUID] {
    def write(t: UUID) = {
      val bos = new ByteArrayOutputStream(16)
      val da = new DataOutputStream(bos)

      try {
        da.writeLong(t.getMostSignificantBits)
        da.writeLong(t.getLeastSignificantBits)
        BSONBinary(bos.toByteArray, UuidSubtype)
      } finally {
        bos.close()
        da.close()
      }
    }

    def read(bson: BSONBinary) = {
      val bb = ByteBuffer.wrap(bson.byteArray)
      val first = bb.getLong
      val second = bb.getLong
      new UUID(first, second)
    }
  }
}
