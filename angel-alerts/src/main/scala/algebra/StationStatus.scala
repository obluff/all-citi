package algebra
import models.BikeStation

trait StationStatus[F[_]] {
  def fetchAll: F[Seq[BikeStation]]
}

object StationStatus {
  def apply[F[_]](implicit status: StationStatus[F]): StationStatus[F] = status
}
