scalaVersion := "2.13.10"
name := "fight"
version := "0.0.1"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"                    % "2.0.5",
  "dev.zio"       %% "zio-json"               % "0.6.2",
  "dev.zio"       %% "zio-http"               % "3.0.0-RC2",
)
libraryDependencies += "org.typelevel" %% "cats-core" % "2.9.0"

// scalaVersion := "3.3.1"

// libraryDependencies ++= Seq(
//   "dev.zio"       %% "zio"            % "2.0.19",
//   "dev.zio"       %% "zio-json"       % "0.6.2",
//   "dev.zio"       %% "zio-http"       % "3.0.0-RC2",
//   "io.getquill"   %% "quill-zio"      % "4.7.0",
//   "io.getquill"   %% "quill-jdbc-zio" % "4.7.0",
//   "com.h2database" % "h2"             % "2.2.224"
// )

// libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.36"