package service

import algebra.Notifier
import cats.effect.Sync
import models.BikeStatus

import org.http4s.{EntityDecoder, Uri}
import org.http4s.client.Client

class NotifierService[F[_]: Sync](
    client: Client[F]
)(implicit D: EntityDecoder[F, Unit])
    extends Notifier[F] {
  override def notify(
      message: String
  ): F[Unit] = {
    val uri = Uri
      .unsafeFromString("https://layer.bicyclesharing.net/map/v1/nyc/stations")
    client.expect[Unit](uri)
  }
}
