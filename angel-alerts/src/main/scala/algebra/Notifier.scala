package algebra

import models.NotifierResponse
import org.http4s.dsl._
import org.http4s.dsl.Http4sDsl
import org.http4s.client.dsl.Http4sClientDsl

trait Notifier[F[_]] {
  val dsl = Http4sClientDsl[F]
  val clientdsl = Http4sDsl[F]

  def notify(message: String): F[NotifierResponse]
}

object Notifier {
  def apply[F[_]](implicit notifier: Notifier[F]): Notifier[F] = notifier
}
