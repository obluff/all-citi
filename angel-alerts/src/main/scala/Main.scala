import cats.effect._
import service.AngelAlertsApp
import org.typelevel.log4cats._
import org.typelevel.log4cats.slf4j._
object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    implicit val logger: Logger[IO] = LoggerFactory[IO].getLogger
    val homeStationId = "150"
    val app = new AngelAlertsApp[IO]
    app.run(homeStationId).map(_ => ExitCode.Success)
  }
}
