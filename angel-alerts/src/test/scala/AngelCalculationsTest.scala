import models.{BikeAngelMeta, BikeAngelsAction}

import org.scalatest._

class AngelCalculationsTest extends funsuite.AnyFunSuite {
  test(
    """Start at a neutral station, bike to a 1-point Drop Off Station - 1 point""""
  ) {
    val source = BikeAngelMeta(BikeAngelsAction.Neutral, 0, 0)
    val dest = BikeAngelMeta(BikeAngelsAction.Take, 1, 1)
    assert(source.transferPoints(dest) == 1)
  }

  test(
    """Trips with Drop Off points at trip start - 0 points"""
  ) {
    val source = BikeAngelMeta(BikeAngelsAction.Take, 0, 0)
    val dest = BikeAngelMeta(BikeAngelsAction.Take, 0, 0)
    assert(source.transferPoints(dest) == 0)
  }

  test(
    """Start at 2-point Pick Up station, bike to neutral station - 2 points"""
  ) {
    val source = BikeAngelMeta(BikeAngelsAction.Give, 2, 2)
    val dest = BikeAngelMeta(BikeAngelsAction.Neutral, 0, 0)
    assert(source.transferPoints(dest) == 2)
  }

  test(
    """Trips with Pick Up points at trip end - 0 points"""
  ) {
    val source = BikeAngelMeta(BikeAngelsAction.Take, 2, 2)
    val dest = BikeAngelMeta(BikeAngelsAction.Take, 0, 0)
    assert(source.transferPoints(dest) == 0)
  }

  test(
    """Start at 4 point Pick Up station, bike to 2-point Drop Off Station - 6 points"""
  ) {
    val source = BikeAngelMeta(BikeAngelsAction.Give, 4, 4)
    val dest = BikeAngelMeta(BikeAngelsAction.Take, 2, 2)
    assert(source.transferPoints(dest) == 6)
  }

  test(
    """Biking from one full station to another full station - 0 points"""
  ) {
    val source = BikeAngelMeta(BikeAngelsAction.Give, 4, 4)
    val dest = BikeAngelMeta(BikeAngelsAction.Give, 4, 4)
    assert(source.transferPoints(dest) == 0)
  }
}
