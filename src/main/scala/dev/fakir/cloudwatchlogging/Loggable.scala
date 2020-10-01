package dev.fakir.cloudwatchlogging

import com.amazonaws.services.logs.AWSLogs
import com.amazonaws.services.logs.model.{InvalidSequenceTokenException, PutLogEventsRequest}
import dev.fakir.cloudwatchlogging.models.CloudWatchLogsProperties
import dev.fakir.cloudwatchlogging.utils.{CloudWatchEnv, CloudWatchEvents}
import org.slf4j.LoggerFactory
import CloudWatchEnv._
import CloudWatchEvents._
import zio.clock.Clock
import zio.{Schedule, Task, UIO, ZIO}

trait Loggable {
  def region: String
  def logGroup: String
  def errorLogStream: String
  def infoLogStream: String
  implicit val watch: AWSLogs = CloudWatchEnv.prepareCloudWatchEnv(region)
  val cloudWatchError: PutLogEventsRequest = getRequestEvent(CloudWatchLogsProperties(logGroup, errorLogStream))
  val cloudWatchInfo: PutLogEventsRequest = getRequestEvent(CloudWatchLogsProperties(logGroup, infoLogStream))
  @transient private lazy val logger = LoggerFactory.getLogger(getClass)
  protected def info[T](msg: Map[String, T]): ZIO[Any with Clock, Throwable, Unit] = {
    for {
      _           <- UIO(logger.info(msg.toString))
      infoEvent   <- Task(buildEvent(msg))
      writeEvent  <- Task(CloudWatchEvents.writeEvents(Seq(infoEvent), cloudWatchInfo))
      _           <- writeEvent.catchSome {
        case e: InvalidSequenceTokenException => writeEvent.retry(Schedule.recurs(5))
      }
    } yield ()
  }
  protected def error[T](msg: Map[String, T]): ZIO[Any with Clock, Throwable, Unit] = {
    for {
      _           <- UIO(logger.error(msg.toString))
      infoEvent   <- Task(buildEvent(msg))
      writeEvent  <- Task(CloudWatchEvents.writeEvents(Seq(infoEvent), cloudWatchError))
      _           <- writeEvent.catchSome {
        case e: InvalidSequenceTokenException => writeEvent.retry(Schedule.recurs(5))
      }
    } yield ()
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
