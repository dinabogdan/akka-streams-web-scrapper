name := "scrapper"

version := "0.1"

scalaVersion := "2.13.2"

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.6"
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.6.6"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.12"

libraryDependencies += "org.jsoup" % "jsoup" % "1.13.1"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.2" % "test"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.6.6" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.6" % Test

