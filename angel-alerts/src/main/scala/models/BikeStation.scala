package models

import scala.math

case class BikeStation(
    meta: StationMeta,
    angelMeta: BikeAngelMeta
) {
  def description: String = {
    s"""${meta.name}
        ${meta.stationId}
        ${meta.coords}
        capacity: ${meta.capacity}
        open docks: ${meta.capacity - meta.bikes}
        bike angel action: ${angelMeta.angelAction}
        bike angel points: ${angelMeta.angelPoints}
    """
  }

  def dist(other: BikeStation): Double = {
    val m = meta.coords
    val n = other.meta.coords
    math.abs(m._1 - n._1) + math.abs(m._2 - n._2)
  }
}

case class StationMeta(
    stationId: String,
    coords: (Double, Double),
    name: String,
    terminal: String,
    capacity: Int,
    bikes: Int,
    docks: Int,
    bikesDisabled: Int,
    docksDisabled: Int
)

case class BikeAngelMeta(
    angelAction: BikeAngelsAction,
    angelPoints: Int, // todo -- wat is the difference between these two
    angelDigits: Int
) {

  def transferPoints(dest: BikeAngelMeta): Int = {
    (angelAction, dest.angelAction) match {
      case (BikeAngelsAction.Give, BikeAngelsAction.Give) => 0
      case (BikeAngelsAction.Give, BikeAngelsAction.Take) =>
        angelPoints + dest.angelPoints
      case (BikeAngelsAction.Give, BikeAngelsAction.Neutral)    => angelPoints
      case (BikeAngelsAction.Neutral, BikeAngelsAction.Neutral) => 0
      case (BikeAngelsAction.Neutral, BikeAngelsAction.Take)    => dest.angelPoints
      case (BikeAngelsAction.Neutral, BikeAngelsAction.Give)    => 0
      case (BikeAngelsAction.Take, _) =>
        0
    }
  }
}

object BikeStation {
  def fromFeaturesOpt(f: Features): Option[BikeStation] = {
    for {
      baa <- f.properties.bike_angels_action
      bap <- f.properties.bike_angels_points
      bad <- f.properties.bike_angels_digits
    } yield (
      BikeStation(
        StationMeta(
          f.properties.station_id,
          f.geometry.coordinates,
          f.properties.name,
          f.properties.terminal,
          f.properties.capacity,
          f.properties.bikes_available,
          f.properties.docks_available,
          f.properties.bikes_disabled,
          f.properties.docks_disabled
        ),
        BikeAngelMeta(
          baa,
          bap,
          bad
        )
      )
    )
  }
}
