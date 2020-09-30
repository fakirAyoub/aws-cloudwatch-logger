package dev.fakir.cloudwatchlogging.models

sealed case class Token(value: String)

object Token {
  case object EMPTY_TOKEN extends Token("")
}