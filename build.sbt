
name := "aws-cloudwatch-logger"

version := "0.1"

scalaVersion := "2.11.11"


libraryDependencies += "com.amazonaws" % "aws-java-sdk-cloudwatch" % "1.11.604"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-logs" % "1.11.601"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.14"


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
