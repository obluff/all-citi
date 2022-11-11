package algebra
import models.BikeStatus

trait StationStatus[F[_]] {
  def fetch(stationId: String): F[BikeStatus]
}

object StationStatus {
  def apply[F[_]](implicit status: StationStatus[F]): StationStatus[F] = status
}
