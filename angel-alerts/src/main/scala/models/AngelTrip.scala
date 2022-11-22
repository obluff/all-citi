package models

case class AngelTrip(
    currentStation: BikeStation,
    pastRides: Seq[AngelRide],
    distanceWalked: Double,
    distanceBiked: Double,
    pointsCollected: Double
) {
  def totalDistance: Double = distanceWalked + distanceBiked

  def moveBike(ride: AngelRide): AngelTrip = {
    this.copy(
      currentStation = ride.dest,
      pastRides = pastRides :+ ride,
      distanceWalked = ride.dest.dist(currentStation),
      distanceBiked = distanceBiked + ride.bikeDist,
      pointsCollected = pointsCollected + ride.points
    )
  }

  def printSummary: String = {
    s"""
       |Total Points: $pointsCollected
       |Distance Walked: $distanceWalked
       |Distance Biked: $distanceBiked
       |Route: ${pastRides.map(_.stationDetails).mkString("\n")}
       |""".stripMargin
  }
}

object AngelTrip {
  def start(first: BikeStation): AngelTrip = {
    AngelTrip(
      first,
      Seq.empty[AngelRide],
      0,
      0,
      0
    )
  }
}

case class AngelRide(
    source: BikeStation,
    dest: BikeStation
) {
  def bikeDist: Double = source.dist(dest)
  def points: Int = source.angelMeta.transferPoints(dest.angelMeta)

  def stationDetails: String =
    s"""${source.meta.name} --> ${dest.meta.name}: points ${points}"""

  def rideDetails: String = {
    s"""
       |Source Station: ${source.meta.name}
       |Dest Station: ${dest.meta.name}
       |
       |POINTS: ${source.angelMeta.transferPoints(dest.angelMeta)}
       |
       |""".stripMargin
  }

}
