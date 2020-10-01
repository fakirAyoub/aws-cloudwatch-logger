package dev.fakir.cloudwatchlogging.utils



sealed trait CloudWatchException {
  self: Throwable =>
  val message: String
  val cause: String
}




case object CloudWatchException {
  case class InvalidSequenceTokenException(message: String, cause: String) extends Throwable with CloudWatchException
  case class DataAlreadyAcceptedException(message: String, cause: String) extends Throwable with CloudWatchException
  case class ResourceNotFoundException(message: String, cause: String) extends Throwable with CloudWatchException
  case class ServiceUnavailableException(message: String, cause: String) extends Throwable with CloudWatchException
  case class UnrecognizedClientException(message: String, cause: String) extends Throwable with CloudWatchException
}