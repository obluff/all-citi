package service

import algebra.Notifier
import cats.effect.Sync
import models.{NotifierResponse, NotifierRequest}
import org.http4s.{EntityDecoder, EntityEncoder, Uri, Request}
import org.http4s.client._

class NotifierService[F[_]: Sync](
    client: Client[F]
)(implicit
    D: EntityDecoder[F, NotifierResponse],
    E: EntityEncoder[F, NotifierRequest]
) extends Notifier[F] {
  import dsl._
  import clientdsl._
  override def notify(
      message: String
  ): F[NotifierResponse] = {
    val uri = Uri
      .unsafeFromString("https://ntfy.sh/angel_alerts")
    Request
    val pr = POST(
      NotifierRequest("Citibike Angel Alert!!!", message),
      uri
    ).withEntity(message)

    client.expect[NotifierResponse](pr)
  }

}
