logLevel := Level.Warn

resolvers += "spark-packages" at "http://dl.bintray.com/spark-packages/maven/"
addSbtPlugin("org.spark-packages" % "sbt-spark-package" % "0.2.2")