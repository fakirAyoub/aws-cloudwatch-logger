package dev.fakir.cloudwatchlogging.utils

import java.util

import com.amazonaws.auth.{AWSStaticCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.services.logs.model._
import com.amazonaws.services.logs.{AWSLogs, AWSLogsClientBuilder}
import dev.fakir.cloudwatchlogging.models.{CloudWatchLogsProperties, Token}

object CloudWatchEnv {
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

  def getNextToken(logStreams: util.List[LogStream], logStreamName: String): String = {
    import scala.collection.JavaConversions._
    logStreams.reduceLeft((_, logStream) =>  if (logStream.getLogStreamName.equals(logStreamName)) logStream.getUploadSequenceToken else Token.EMPTY_TOKEN.value)
  }

  def prepareCloudWatchEnv(region: String): AWSLogs = {
    val credentials = new DefaultAWSCredentialsProviderChain
    AWSLogsClientBuilder.standard()
      .withCredentials(new AWSStaticCredentialsProvider(credentials.getCredentials))
      .withRegion(region).build()
  }
}

