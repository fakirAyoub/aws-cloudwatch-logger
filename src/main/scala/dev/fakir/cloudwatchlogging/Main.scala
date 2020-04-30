package dev.fakir.cloudwatchlogging

object Main extends Loggable {
  override def region: String = ???

  override def logGroup: String = ???

  override def errorLogStream: String = ???

  override def infoLogStream: String = ???
}
