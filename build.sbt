scalaVersion := "2.13.10"
name := "fight"
version := "0.0.1"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.0.5",
  "dev.zio" %% "zio-json" % "0.6.2",
  "dev.zio" %% "zio-http" % "3.0.0-RC2"
)
libraryDependencies += "org.typelevel" %% "cats-core" % "2.9.0"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-test" % "2.0.20" % Test,
  "dev.zio" %% "zio-test-sbt" % "2.0.20" % Test,
  "dev.zio" %% "zio-test-magnolia" % "2.0.20" % Test
)
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
