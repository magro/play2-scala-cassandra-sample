name := "play2-scala-cassandra-sample"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  cache,
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.0.1"
)     

play.Project.playScalaSettings
