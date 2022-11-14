package models

/* Raw JSON from API that feeds the Citibike app (not open bikeshare feed) */

case class BikeStatus(
    `type`: String,
    features: Seq[Features]
)

case class Features(
    `type`: String,
    geometry: Geometry,
    properties: Properties
)

case class Geometry(
    `type`: String,
    coordinates: (Double, Double) //[Double]
)

case class Properties(
    station_id: String,
    name: String,
    terminal: String,
    capacity: Int,
    bikes_available: Int,
    docks_available: Int,
    bikes_disabled: Int,
    docks_disabled: Int,
    renting: Boolean,
    returning: Boolean,
    ebike_surcharge_waiver: Option[Boolean],
    bike_angels_action: Option[BikeAngelsAction],
    bike_angels_points: Option[Int],
    bike_angels_digits: Option[Int],
    ebikes_available: Option[Int],
    installed: Boolean,
    last_reported: Int,
    icon_pin_bike_layer: String,
    icon_pin_dock_layer: String,
    icon_dot_bike_layer: String,
    icon_dot_dock_layer: String,
    valet_status: String
)

sealed trait BikeAngelsAction {
  val value: String
  val mag: Int
}

object BikeAngelsAction {
  final case object Give extends BikeAngelsAction {
    val value = "give"
    val mag = -1
  }
  final case object Take extends BikeAngelsAction {
    val value = "take"
    val mag = 1
  }
  final case object Neutral extends BikeAngelsAction {
    val value = "neutral"
    val mag = 0
  }
  def fromString: PartialFunction[String, BikeAngelsAction] = {
    case "give"    => Give
    case "take"    => Take
    case "neutral" => Neutral
  }
}
