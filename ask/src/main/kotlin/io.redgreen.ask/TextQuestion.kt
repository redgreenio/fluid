package io.redgreen.ask

import io.redgreen.ask.ValidationResult.Success

class TextQuestion(
  val text: String,
  val default: String? = null,
  val validator: Validator = ACCEPT_ALL
) {
  companion object {
    val ACCEPT_ALL: (String) -> ValidationResult = { text -> Success(text) }
  }
}
