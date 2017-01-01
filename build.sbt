name := "learning-scala"

lazy val commonSettings = Seq(
  organization := "com.fybai",
  version := "0.1.0",
  scalaVersion := "2.12.1"
)

lazy val akka_library = Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.16",
  "com.typesafe.akka" %% "akka-agent" % "2.4.16",
  "com.typesafe.akka" %% "akka-camel" % "2.4.16",
  "com.typesafe.akka" %% "akka-cluster" % "2.4.16",
  "com.typesafe.akka" %% "akka-cluster-metrics" % "2.4.16",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.4.16",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.16",
  "com.typesafe.akka" %% "akka-contrib" % "2.4.16",
  "com.typesafe.akka" %% "akka-multi-node-testkit" % "2.4.16",
  "com.typesafe.akka" %% "akka-osgi" % "2.4.16",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.16",
  "com.typesafe.akka" %% "akka-persistence-tck" % "2.4.16",
  "com.typesafe.akka" %% "akka-remote" % "2.4.16",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.16",
  "com.typesafe.akka" %% "akka-stream" % "2.4.16",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.16",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.16",
  "com.typesafe.akka" %% "akka-distributed-data-experimental" % "2.4.16",
  "com.typesafe.akka" %% "akka-typed-experimental" % "2.4.16",
  "com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.16",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "junit" % "junit" % "4.12" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)

lazy val play_library = Seq(

)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= akka_library
  )

lazy val hello_akka = (project in file("hello-akka")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= akka_library
  )

lazy val hello_scala = (project in file("hello-scala")).
  settings(commonSettings: _*)


lazy val hello_play = (project in file("hello-play")).
  settings(commonSettings: _*)