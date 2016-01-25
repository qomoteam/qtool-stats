import sbt.Keys._

name := "qtool-stats"

version := "1.0"

scalaVersion := "2.10.6"

spName := "cbb/qtool-stats"
sparkVersion := "1.6.0"

libraryDependencies ++= Seq(
  //"org.apache.spark" %% "spark-core" % "1.6.0",
  "org.apache.commons" % "commons-math3" % "3.6",
  "com.github.scopt" %% "scopt" % "3.3.0",

  "org.scalatest" % "scalatest_2.10" % "2.2.4" % "test"
).map(_.force())
