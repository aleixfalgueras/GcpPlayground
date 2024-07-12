package com.demos.mis
import com.utils.DateTimeUtils
import com.utils.DateTimeUtils.SIMPLE_DATE_FORMAT
import io.circe._
import io.circe.generic.semiauto._
import io.circe.parser._

import java.time.LocalDate
import scala.util.{Failure, Success, Try}

case class Person(name: String, age: Int, birthDate: LocalDate)

object CircleDemo extends App {

  implicit val personDecoder: Decoder[Person] = deriveDecoder[Person]

  implicit val localDateDecoder: Decoder[LocalDate] =
    Decoder.decodeString.emap { stringLocalDate: String =>
      val localDateParsed: Try[LocalDate] = Try(DateTimeUtils.getDate(stringLocalDate, SIMPLE_DATE_FORMAT))

      localDateParsed match {
        case Success(localDate: LocalDate) => Right(localDate)
        case Failure(e: Throwable) => Left(e.getMessage)
      }

    }

  val personJson: String =
    """
      |{
      |  "name": "John Doe",
      |  "age": 30,
      |  "birthDate": "21-07-1996"
      |}
    """.stripMargin

  val decodedPerson: Either[Error, Person] = decode[Person](personJson)

  decodedPerson match {
    case Right(person) => println(s"Decoded person: $person")
    case Left(error) => println(s"Failed to decode: $error")
  }

}
