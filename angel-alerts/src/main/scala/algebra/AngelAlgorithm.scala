package algebra
import models.{AngelTrip, BikeStation}

/* if there is no generic type is it still considered an Algebra? */

trait AngelAlgorithm {
  def findTrip(
      trip: AngelTrip,
      otherStations: Seq[BikeStation],
      length: Int
  ): AngelTrip

}
