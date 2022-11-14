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
    } yield findClosestStations(hs, allStations)
      .map(_.description)
      .mkString("\n\n")).getOrElse("unable to find home station")
  }

  private def findClosestStations(
      homeStation: BikeStation,
      otherStations: Seq[BikeStation]
  ): Seq[BikeStation] = {
    otherStations
      .sortBy(x => dist(homeStation.meta.coords, x.meta.coords))
      .take(20)
      .sortBy(x => homeStation.angelMeta.transferPoints(x.angelMeta))
//      .take(10)
  }

  private def dist(m: (Double, Double), n: (Double, Double)): Double = {
    math.sqrt(math.pow(m._1 - n._1, 2) + math.pow(m._2 - n._2, 2))
  }

}
