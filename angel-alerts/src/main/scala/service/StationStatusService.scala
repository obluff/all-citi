package service
import cats.effect.Sync
import cats.implicits._
import algebra.StationStatus
import models.BikeStatus
import org.http4s.{EntityDecoder, Uri}
import org.http4s.client.Client
import models.{BikeStatus, BikeStation}

class StationStatusService[F[_]: Sync](
    client: Client[F]
)(implicit D: EntityDecoder[F, BikeStatus])
    extends StationStatus[F] {
  override def fetchAll: F[Seq[BikeStation]] = {
    // don't feel like looking up this syntax rn
    val uri = Uri
      .unsafeFromString("https://layer.bicyclesharing.net/map/v1/nyc/stations")
    client.expect[BikeStatus](uri).map { bs =>
      bs.features.flatMap(BikeStation.fromFeaturesOpt)
    }
  }
}
