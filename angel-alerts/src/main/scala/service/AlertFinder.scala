package service
import service.{NotifierService, StationStatusService}
import cats.effect.Sync
import cats.implicits._
import models.{BikeStatus, Features}
import org.typelevel.log4cats.Logger

class AlertFinder[F[_]: Sync: Logger](
    stationStatus: StationStatusService[F],
    notifier: NotifierService[F]
) {
  def alert(
      homeStationId: String
  ): F[Unit] = {
    for {
      status <- stationStatus.fetch(homeStationId)
      routes = printBestRoutes(status, homeStationId)
      _ <- Logger[F].info(routes)
      _ <- notifier.notify(routes)
    } yield ()
  }

  private def printBestRoutes(bs: BikeStatus, homeStationId: String): String = {
    (for {
      hs <- findHomeStation(bs, homeStationId)
    } yield findClosestStations(hs, bs.features)
      .map(_.description)
      .mkString("\n\n")).getOrElse("unable to find home station")
  }

  private def findHomeStation(
      bs: BikeStatus,
      stationId: String
  ): Option[Features] = bs.features.find(_.properties.station_id == stationId)

  private def findClosestStations(
      homeStation: Features,
      otherStations: Seq[Features]
  ): Seq[Features] = {
    otherStations
      .sortBy(x =>
        dist(homeStation.geometry.coordinates, x.geometry.coordinates)
      )
      .take(100)
      .sortBy(x => scoreAngels(homeStation, x))
      .take(10)
  }

  private def scoreAngels(
      home: Features,
      dest: Features
  ): Int = {
    (for {
      ha <- home.properties.bike_angels_action
      hp <- home.properties.bike_angels_points
      da <- dest.properties.bike_angels_action
      dp <- dest.properties.bike_angels_points
    } yield (ha.mag * hp + da.mag * -1 * dp)).getOrElse(0)
  }

  private def dist(m: (Double, Double), n: (Double, Double)): Double = {
    math.sqrt(math.pow(m._1 - n._1, 2) + math.pow(m._2 - n._2, 2))
  }

}
