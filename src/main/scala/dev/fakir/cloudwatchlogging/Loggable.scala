package dev.fakir.cloudwatchlogging

import com.amazonaws.services.logs.AWSLogs
import com.amazonaws.services.logs.model.PutLogEventsRequest
import dev.fakir.cloudwatchlogging.models.CloudWatchLogsProperties
import dev.fakir.cloudwatchlogging.utils.{CloudWatchEnv, CloudWatchEvents}
import org.slf4j.LoggerFactory
import CloudWatchEnv._
import CloudWatchEvents._
import zio.{UIO, ZIO}

trait Loggable {
  def region: String
  def logGroup: String
  def errorLogStream: String
  def infoLogStream: String
  implicit val watch: AWSLogs = CloudWatchEnv.prepareCloudWatchEnv(region)
  val cloudWatchError: PutLogEventsRequest = getRequestEvent(CloudWatchLogsProperties(logGroup, errorLogStream))
  val cloudWatchInfo: PutLogEventsRequest = getRequestEvent(CloudWatchLogsProperties(logGroup, infoLogStream))
  @transient private lazy val logger = LoggerFactory.getLogger(getClass)
  protected def info[T](msg: Map[String, T]): UIO[Unit] = {
    logger.info(msg.toString)
    val infoEvent = buildEvent(msg)
    UIO(CloudWatchEvents.writeEvents(Seq(infoEvent), cloudWatchInfo))
  }
  protected def error[T](msg: Map[String, T]): Unit = {
    logger.error(msg.toString())
    val infoEvent = buildEvent(msg)
    UIO(writeEvents(Seq(infoEvent), cloudWatchError))
  }

  protected def info(msg: String): Unit = {
    logger.info(msg)
    UIO(writeEvent(msg, cloudWatchInfo))
  }
  protected def error(msg: String): Unit = {
    logger.error(msg)
    UIO(writeEvent(msg, cloudWatchError))
  }
}
