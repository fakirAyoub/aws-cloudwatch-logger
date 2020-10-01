
name := "aws-cloudwatch-logger"

version := "0.1"

scalaVersion := "2.11.8"


libraryDependencies += "com.amazonaws" % "aws-java-sdk-cloudwatch" % "1.11.604"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-logs" % "1.11.601"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.14"
libraryDependencies += "dev.zio" %% "zio" % "1.0.1"

organization in ThisBuild := "dev.fakir"

name := "aws-cloudwatch-logger"

homepage := Some(url("https://github.com/fakirAyoub/aws-cloudwatch-logger"))
licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
publishMavenStyle := true
publishArtifact in Test := false

credentials += Credentials(Path.userHome / ".sbt" / "sonatype_credentials")

pomIncludeRepository := { _ => false }
publishTo in ThisBuild := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
scmInfo := Some(
  ScmInfo(
    url("https://github.com/fakirAyoub/aws-cloudwatch-logger"),
    "scm:git:git@github.com:fakirAyoub/aws-cloudwatch-logger.git"
  )
)
developers := List(
  Developer("fakirAyoub", "Ayoub Fakir", "ayoub@fakir.dev", url("https://www.fakir.dev"))
)
/*assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}*/
