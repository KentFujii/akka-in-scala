name := "testdriven"

version := "1.0"

scalaVersion := "2.11.12"

libraryDependencies ++= {
  val akkaVersion = "2.4.19"
  Seq(
    "com.typesafe.akka"       %%  "akka-actor"                     % akkaVersion,
    "com.typesafe.akka"       %%  "akka-slf4j"                     % akkaVersion,
    "com.typesafe.akka"       %%  "akka-testkit"                   % akkaVersion   % "test",
    "org.scalatest"           %% "scalatest"                       % "3.0.0"       % "test",
    "com.github.nscala-time"  %%  "nscala-time"                    % "1.8.0"
  )
}