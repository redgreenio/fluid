package io.redgreen.ask

import io.redgreen.ask.Answer.DefaultText
import io.redgreen.ask.Answer.UserText
import io.redgreen.ask.Answer.ValidationFailure
import io.redgreen.ask.ValidationResult.Failure

fun ask(
  question: TextQuestion,
  getInput: (TextQuestion) -> String
): Answer {
  val input = getInput(question)
  val validationResult = question.validator(input)
  return answer(question, input, validationResult)
}

private fun answer(
  question: TextQuestion,
  input: String,
  validationResult: ValidationResult
): Answer {
  val userInputFailedValidation = validationResult is Failure && !input.isBlank()
  val questionHasDefaultAndUserInputIsBlank = input.isBlank() && question.default != null

  return when {
    userInputFailedValidation -> ValidationFailure(input, (validationResult as Failure).message)
    questionHasDefaultAndUserInputIsBlank -> DefaultText(question.default!!)
    else -> UserText(input.trim())
  }
}
