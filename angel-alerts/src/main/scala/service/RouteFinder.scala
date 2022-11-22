package service
import algebra.{StationStatus, Notifier}
import cats.effect.Sync
import cats.implicits._
import models.{BikeStation, AngelTrip}

import AngelAlgorithm._

import org.typelevel.log4cats.Logger
class RouteFinder[F[_]: Sync: Logger: StationStatus: Notifier](
) {
  def alert(
      homeStationId: String,
      numRides: Int
  ): F[Unit] = {
    for {
      status <- StationStatus[F].fetch
      _ <- Logger[F].info(s"number of bikes ${status.size}")
      routes = printBestRoutes(status, homeStationId, numRides)
      _ <- Logger[F].info(routes)
      nr <- Notifier[F].notify(routes)
      _ <- Logger[F].info(nr.toString)
    } yield ()
  }

  private def printBestRoutes(
      allStations: Seq[BikeStation],
      homeStationId: String,
      numRides: Int
  ): String = {
    (for {
      hs <- allStations.find(_.meta.stationId == homeStationId)
      filteredStations = allStations.sortBy(hs.dist).take(30)
    } yield AngelAlgorithm.ExponentialImplementation
      .findTrip(AngelTrip.start(hs), filteredStations, numRides)
      .printSummary)
      .getOrElse("unable to find home station")
  }

}
