scalaVersion := "2.13.8" // Also supports 2.12.x and 3.x
val http4sVersion = "0.23.16"
val circeVersion = "0.14.1"
val scalaTestVersion = "3.2.10"
// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
)

addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full
)
