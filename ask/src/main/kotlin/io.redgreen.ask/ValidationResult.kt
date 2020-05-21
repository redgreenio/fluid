package io.redgreen.ask

sealed class ValidationResult(
  open val value: String
) {
  data class Success(
    override val value: String
  ) : ValidationResult(value)

  data class Failure(
    override val value: String,
    val message: String
  ) : ValidationResult(value)
}
