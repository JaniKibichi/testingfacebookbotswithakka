enablePlugins(JavaServerAppPackaging)

name := "airtime-fsm"

version := "1.0"

scalaVersion := "2.12.8"

lazy val akkaVersion = "2.6.0-M2"

val circeVersion = "0.10.0"

herokuAppName in Compile := "airtime-fsm"

resolvers += "africastalking maven repository" at "http://dl.bintray.com/africastalking/java"

resolvers += "digidev - repository" at "https://dl.bintray.com/digidevbr/messenger4j/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.google.firebase" % "firebase-admin" % "6.7.0",
  "com.google.code.gson" % "gson" % "2.8.2",
  "org.json" % "json" % "20160810",  
  "com.typesafe.akka" %% "akka-http-core" % "10.1.8",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8",
  "com.typesafe.akka" %% "akka-http" % "10.1.8",
  "com.africastalking" % "core" % "3.4.0",
  "ch.qos.logback" % "logback-classic" % "1.3.0-alpha4" % Test,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic"% circeVersion,
  "io.circe" %% "circe-parser"% circeVersion,
  "de.heikoseeberger" %% "akka-http-circe" % "1.25.2",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

mainClass in assembly := Some("com.janikibichi.Main")

assemblyJarName in assembly := "airtime-fsm.jar"
