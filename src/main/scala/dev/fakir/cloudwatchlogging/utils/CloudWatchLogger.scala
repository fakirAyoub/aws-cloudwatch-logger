package dev.fakir.cloudwatchlogging.utils

import java.util

import com.amazonaws.auth.{AWSStaticCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.services.logs.model._
import com.amazonaws.services.logs.{AWSLogs, AWSLogsClientBuilder}
import dev.fakir.cloudwatchlogging.models.{CloudWatchLogsProperties, Token}

object CloudWatchLogger {
  def getRequestEvent(cloudWatchLogsProperties: CloudWatchLogsProperties)(implicit watch: AWSLogs): PutLogEventsRequest = {
    val logStreamsRequest = new DescribeLogStreamsRequest(cloudWatchLogsProperties.logGroupName)
    val logStreams: util.List[LogStream] = watch.describeLogStreams(logStreamsRequest).getLogStreams
    val token = getNextToken(logStreams, cloudWatchLogsProperties.logStreamName)
    val request = new PutLogEventsRequest()
    request.setLogGroupName(cloudWatchLogsProperties.logGroupName)
    request.setLogStreamName(cloudWatchLogsProperties.logStreamName)
    request.setSequenceToken(token)
    request
  }

  def writeEvents(events: Seq[InputLogEvent], request: PutLogEventsRequest)(implicit watch: AWSLogs): PutLogEventsResult = {
    val readyEvents = appendEvents(events)
    val requestToSend = getRequestEvent(CloudWatchLogsProperties(request.getLogGroupName, request.getLogStreamName))
    requestToSend.setLogEvents(readyEvents)
    watch.putLogEvents(requestToSend)
  }

  def getNextToken(logStreams: util.List[LogStream], logStreamName: String): String = {

    import scala.collection.JavaConversions._

    logStreams.reduceLeft((_, logStream) =>  if (logStream.getLogStreamName.equals(logStreamName)) logStream.getUploadSequenceToken else Token.EMPTY_TOKEN.value)

  }

  def buildEvent[T](log: Map[String, T]*): InputLogEvent = {
    new InputLogEvent().withMessage(log.mkString(":")).withTimestamp(System.currentTimeMillis)
  }
  def appendEvents(events: Seq[InputLogEvent]): util.ArrayList[InputLogEvent] = {
    val logEvents = new util.ArrayList[InputLogEvent]()
    events.map(event => {
      logEvents.add(event)
    })
    logEvents
  }

  def awsCloudWatch(region: String): AWSLogs = {
    val credentials = new DefaultAWSCredentialsProviderChain
    AWSLogsClientBuilder.standard()
      .withCredentials(new AWSStaticCredentialsProvider(credentials.getCredentials))
      .withRegion(region).build()
  }
}

