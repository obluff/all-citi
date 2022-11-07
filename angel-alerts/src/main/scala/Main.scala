import cats.effect._
import org.http4s.ember.client._
import org.http4s.client._
import service.AngelAlerts

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val homeStationId = "150"
    EmberClientBuilder.default[IO].build.use { cli =>
      AngelAlerts
        .fetchStationInfo(cli, homeStationId)
        .flatTap(IO.println)
        .as(ExitCode.Success)
    }
  }

}
