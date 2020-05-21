package io.redgreen.ask

sealed class Answer(
  open val value: String
) {
  data class UserText(
    override val value: String
  ) : Answer(value)

  data class DefaultText(
    override val value: String
  ) : Answer(value)

  data class ValidationFailure(
    override val value: String,
    val message: String
  ) : Answer(value)
}
