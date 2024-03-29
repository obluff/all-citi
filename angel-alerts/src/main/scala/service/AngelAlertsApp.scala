package service

import cats.effect.Async
import io.circe.Decoder
import io.circe.generic.auto._
import models.{BikeAngelsAction, BikeStatus, NotifierRequest, NotifierResponse}
import org.typelevel.log4cats.Logger
import org.http4s._
import org.http4s.circe._
import org.http4s.ember.client._

import scala.util.Try

class AngelAlertsApp[F[_]: Async: Logger] {
  implicit val decodeInstant: Decoder[BikeAngelsAction] =
    Decoder.decodeString.emapTry { str =>
      Try(BikeAngelsAction.fromString(str))
    }

  implicit val decoder: EntityDecoder[F, BikeStatus] =
    jsonOf[F, BikeStatus]

  implicit val notifierDecoder: EntityDecoder[F, NotifierResponse] =
    jsonOf[F, NotifierResponse]

  implicit val notifierEncoder: EntityEncoder[F, NotifierRequest] =
    jsonEncoderOf[F, NotifierRequest]

  def run(hsId: String): F[Unit] = {
    EmberClientBuilder.default[F].build.use { cli =>
      implicit val af = new StationStatusService[F](cli)
      implicit val nf = new NotifierService[F](cli)
      val alertFinder = new RouteFinder[F]()
      alertFinder.alert(hsId, 6)
    }
  }
}
