## Description
This is a small AWS CloudWatch Logger wrapper library allowing
you to push logs to CloudWatch's Log Streams. It is useful when running EMR
jobs.

## Usage
```scala
object Main extends Loggable {
  override def region: String = ???

  override def logGroup: String = ???

  override def errorLogStream: String = ???

  override def infoLogStream: String = ???
}

```