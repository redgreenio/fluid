package io.redgreen.fluid.prompts

import io.redgreen.ask.ValidationResult.Failure
import io.redgreen.ask.ValidationResult.Success
import io.redgreen.ask.Validator

val KEBAB_CASE: Validator by lazy { getKebabCaseValidator() }

private fun getKebabCaseValidator(): Validator = { text: String ->
  if (isValidKebabCase(text)) {
    Success(text)
  } else {
    val failureMessage = "Should start with an alphabet and can contain alphabets (lowercase), numbers, and hyphens (-)."
    Failure(text, failureMessage)
  }
}

private fun isValidKebabCase(text: String): Boolean {
  val startsWithLetter = text.isNotBlank() && text.first().isLetter()
  val endsWithLetterOrDigit = text.isNotBlank() && text.last().isLetterOrDigit()
  val disallowedChars: (Char) -> Boolean = { char -> if (char == '-') false else !char.isLetterOrDigit() }
  val containsOnlyAlphabetsDigitsHyphens = text.find(disallowedChars) == null

  return startsWithLetter && endsWithLetterOrDigit && containsOnlyAlphabetsDigitsHyphens
}
