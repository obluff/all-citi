package service
import algebra.AngelAlgorithm
import scala.annotation.tailrec
import models.{AngelTrip, AngelRide, BikeStation}

object AngelAlgorithm {
  object AngelAlgoBasic extends AngelAlgorithm {
    def findTrip(
        sourceTrip: AngelTrip,
        allStations: Seq[BikeStation],
        length: Int
    ): AngelTrip = {
      (1 to length).foldLeft[AngelTrip](sourceTrip)((trip, _) =>
        nextBestTripHelper(trip, allStations)
      )
    }

    private def nextBestTripHelper(
        trip: AngelTrip,
        otherStations: Seq[BikeStation]
    ): AngelTrip = {
      val homeStation = trip.currentStation
      val availableStations = (otherStations.toSet -- trip.pastRides
        .flatMap(x => Seq(x.source, x.dest))
        .toSet).toSeq

      val nextTrip = availableStations
        .sortBy(homeStation.dist)
        .take(50)
        .combinations(2)
        .map(x => (x(0), x(1)))
        .toSeq
        .sortBy { case (hs, dest) =>
          (
            hs.angelMeta.transferPoints(dest.angelMeta),
            -1 * homeStation.dist(dest)
          )
        }
        .reverse
        .head
      val best = AngelRide(nextTrip._1, nextTrip._2)
      trip.moveBike(best)
    }
  }

  object ExponentialImplementation extends AngelAlgorithm {
    def findTrip(
        trip: AngelTrip,
        otherStations: Seq[BikeStation],
        length: Int
    ) = {
      nextTripHelper(
        trip,
        otherStations,
        length
      )
    }

    private def nextTripHelper(
        trip: AngelTrip,
        otherStations: Seq[BikeStation],
        turnsLeft: Int
    ): AngelTrip = {
      if (trip.pastRides.size == turnsLeft) {
        trip
      } else {
        val last = trip.pastRides.lastOption
          .map(x => Set(x.source, x.dest))
          .getOrElse(Set.empty[BikeStation])

        val availableStations =
          (otherStations.toSet -- last).toSeq
        val possibilities =
          availableStations
            .combinations(2)
            .map(x => AngelRide(x(0), x(1)))
            .toSeq
            .sortBy(x => -1 * x.points)
            .take(10)

        possibilities
          .map { newRide =>
            nextTripHelper(
              trip.moveBike(newRide),
              availableStations,
              turnsLeft
            )
          }
          .sortBy(x => (-1 * x.pointsCollected, -1 * x.distanceWalked))
          .head
      }
    }
  }

  /* teehee */
  object FactorialImplementation extends AngelAlgorithm {
    def findTrip(
        trip: AngelTrip,
        otherStations: Seq[BikeStation],
        length: Int
    ) = {
      nextTripHelper(
        trip,
        otherStations.sortBy(trip.currentStation.dist),
        length
      )
    }

    private def nextTripHelper(
        trip: AngelTrip,
        otherStations: Seq[BikeStation],
        turnsLeft: Int
    ): AngelTrip = {
      if (trip.pastRides.size == turnsLeft) {
        trip
      } else {
        val last = trip.pastRides.lastOption
          .map(x => Set(x.source, x.dest))
          .getOrElse(Set.empty[BikeStation])
        val availableStations =
          (otherStations.toSet -- last).toSeq
        val possibilities =
          availableStations
            .combinations(2)
            .map(x => AngelRide(x(0), x(1)))
            .toSeq

        possibilities
          .map { newRide =>
            nextTripHelper(
              trip.moveBike(newRide),
              availableStations,
              turnsLeft
            )
          }
          .sortBy(x => (-1 * x.pointsCollected, -1 * x.distanceWalked))
          .head
      }
    }
  }
}
