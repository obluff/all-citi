package algebra

trait Notifier[F[_]] {
  def notify(message: String): F[Unit]
}

object Notifier {
  def apply[F[_]](implicit notifier: Notifier[F]): Notifier[F] = notifier
}
