package service
import algebra.{StationStatus, Notifier}
import cats.effect.Sync
import cats.implicits._
import models.{BikeStation, BikeAngelMeta, BikeAngelsAction}
import org.typelevel.log4cats.Logger
class AlertFinder[F[_]: Sync: Logger: StationStatus: Notifier](
) {
  def alert(
      homeStationId: String
  ): F[Unit] = {
    for {
      status <- StationStatus[F].fetchAll
      _ <- Logger[F].info(s"number of bikes ${status.size}")
      routes = printBestRoutes(status, homeStationId)
      _ <- Logger[F].info(routes)
      _ <- Notifier[F].notify(routes)
    } yield ()
  }

  private def printBestRoutes(
      allStations: Seq[BikeStation],
      homeStationId: String
  ): String = {
    (for {
      hs <- allStations.find(_.meta.stationId == homeStationId)
    } yield findClosestStations(hs, allStations))
      .getOrElse("unable to find home station")
  }

  private def findClosestStations(
      homeStation: BikeStation,
      otherStations: Seq[BikeStation]
  ): String = {
    otherStations
      .sortBy(x => dist(homeStation.meta.coords, x.meta.coords))
      .take(50)
      .combinations(2)
      .map(x => (x(0), x(1)))
      .toSeq
      .sortBy { case (hs, dest) =>
        (
          hs.angelMeta.transferPoints(dest.angelMeta),
          -1 * dist(homeStation.meta.coords, dest.meta.coords)
        )
      }
      .reverse
      .take(10)
      .map({ case (hs, dest) => printRouteOption(hs, dest) })
      .mkString("\n\n")
  }

  private def dist(m: (Double, Double), n: (Double, Double)): Double = {
    math.sqrt(math.pow(m._1 - n._1, 2) + math.pow(m._2 - n._2, 2))
  }

  private def printRouteOption(hs: BikeStation, ds: BikeStation): String = {
    s"""
       |Source Station: ${hs.meta.name}
       |Match Station: ${ds.meta.name}
       |
       |POINTS: ${hs.angelMeta.transferPoints(ds.angelMeta)}
       |
       |""".stripMargin
  }
}
