package dev.fakir.cloudwatchlogging.utils



sealed trait CloudWatchException {
  self: Throwable =>
  val message: String
  val cause: String
}




case object CloudWatchException {
  case class InvalidSequenceTokenException(message: String, cause: String) extends Throwable with CloudWatchException
}