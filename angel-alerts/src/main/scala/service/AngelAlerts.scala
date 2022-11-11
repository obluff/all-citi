package service
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.client.dsl.io._
import org.http4s.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
import org.http4s.client._
import scala.math
import scala.util.Try

import models.{BikeStatus, Features, BikeAngelsAction}
import org.http4s.EntityDecoder

object AngelAlerts {

  implicit val decodeInstant: Decoder[BikeAngelsAction] =
    Decoder.decodeString.emapTry { str =>
      Try(BikeAngelsAction.fromString(str))
    }

  implicit val bikeStatusDecoder: EntityDecoder[IO, BikeStatus] =
    jsonOf[IO, BikeStatus]

  def run(cli: Client[IO], homeStationId: String): IO[String] = {
    fetchData(cli).map(printBestRoutes(_, homeStationId))
  }

  private def fetchData(cli: Client[IO]): IO[BikeStatus] = {
    // todo -- make safe :lol:
    val uri = Uri
      .unsafeFromString("https://layer.bicyclesharing.net/map/v1/nyc/stations")
    val req = GET(uri)
    cli.expect[BikeStatus](req)
  }

  private def printBestRoutes(bs: BikeStatus, homeStationId: String): String = {
    (for {
      hs <- findHomeStation(bs, homeStationId)
    } yield (
      findClosestStations(hs, bs.features).map(_.description).mkString("\n\n")
    )).getOrElse("unable to find home station")
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
