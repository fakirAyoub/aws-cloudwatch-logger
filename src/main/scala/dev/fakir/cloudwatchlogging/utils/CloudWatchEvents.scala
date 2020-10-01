package dev.fakir.cloudwatchlogging.utils

import java.util

import com.amazonaws.services.logs.AWSLogs
import com.amazonaws.services.logs.model.{InputLogEvent, PutLogEventsRequest, PutLogEventsResult}
import dev.fakir.cloudwatchlogging.models.CloudWatchLogsProperties
import dev.fakir.cloudwatchlogging.utils.CloudWatchEnv.getRequestEvent
import dev.fakir.cloudwatchlogging.utils.CloudWatchException.InvalidSequenceTokenException
import zio.{Task, ZIO}

object CloudWatchEvents {
  def buildEvent[T](log: Map[String, T]*): InputLogEvent = {
    new InputLogEvent().withMessage(log.mkString(":")).withTimestamp(System.currentTimeMillis)
  }

  def appendEvents(events: Seq[InputLogEvent]): util.ArrayList[InputLogEvent] = {
    val logEvents = new util.ArrayList[InputLogEvent]()
    events.map(logEvents.add)
    logEvents
  }

  def prepareEvent(event: String): util.ArrayList[InputLogEvent] = {
    val logEvent = new InputLogEvent().withMessage(event)
    val logEvents = new util.ArrayList[InputLogEvent]()
    logEvents.add(logEvent)
    logEvents
  }

  def writeEvents(events: Seq[InputLogEvent], request: PutLogEventsRequest)(implicit watch: AWSLogs): ZIO[Any, Throwable, PutLogEventsResult] = {
    val readyEvents = appendEvents(events)
    val requestToSend = getRequestEvent(CloudWatchLogsProperties(request.getLogGroupName, request.getLogStreamName))
    requestToSend.setLogEvents(readyEvents)
    Task(watch.putLogEvents(requestToSend))
  }

  def writeEvent(event: String, request: PutLogEventsRequest)(implicit watch: AWSLogs): ZIO[Any, Throwable, PutLogEventsResult] = {
    val requestToSend = getRequestEvent(CloudWatchLogsProperties(request.getLogGroupName, request.getLogStreamName))
    requestToSend.setLogEvents(prepareEvent(event))

    Task(watch.putLogEvents(requestToSend)).catchSome {
      case i: InvalidSequenceTokenException => ZIO.fail(InvalidSequenceTokenException(i.message, i.cause))
    }
  }
}
