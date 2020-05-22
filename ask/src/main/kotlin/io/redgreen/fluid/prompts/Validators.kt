package io.redgreen.fluid.prompts

import io.redgreen.ask.ValidationResult.Failure
import io.redgreen.ask.ValidationResult.Success
import io.redgreen.ask.Validator

private const val HYPHEN = '-'
private const val UNDERSCORE = '_'
private const val DOT = '.'

val KEBAB_CASE: Validator by lazy { getKebabCaseValidator() }
val PACKAGE_NAME: Validator by lazy { getPackageNameValidator() }

private fun getKebabCaseValidator(): Validator = { text: String ->
  if (isValidKebabCase(text)) {
    Success(text)
  } else {
    val message = "Should start with an alphabet and can contain alphabets (lowercase), numbers, and hyphens (-)."
    Failure(text, message)
  }
}

private fun isValidKebabCase(text: String): Boolean {
  val startsWithLetter = text.isNotBlank() && text.first().isLetter()
  val endsWithLetterOrDigit = text.isNotBlank() && text.last().isLetterOrDigit()
  val illegalChars: (Char) -> Boolean = { char -> char != HYPHEN && !char.isLetterOrDigit() }
  val containsIllegalChars = text.find(illegalChars) != null

  return startsWithLetter && endsWithLetterOrDigit && !containsIllegalChars
}

private fun getPackageNameValidator(): Validator = { packageName: String ->
  if (isValidPackageName(packageName)) {
    Success(packageName)
  } else {
    val message = "Can contain alphabets, numbers (preceded by an alphabet or underscore)," +
      " underscores (_) and dots."
    Failure(packageName, message)
  }
}

private fun isValidPackageName(packageName: String): Boolean {
  val namespaceSegments = packageName.split(DOT)
  val firstCharIsLetterOrUnderscore = namespaceSegments
    .map(::firstCharIsLetterOrUnderscore)
    .none { !it }

  val illegalChars: (Char) -> Boolean = { char -> char != UNDERSCORE && char != DOT && !char.isLetterOrDigit() }
  val containIllegalChars = packageName.find(illegalChars) != null
  return firstCharIsLetterOrUnderscore && !containIllegalChars
}

private fun firstCharIsLetterOrUnderscore(text: String): Boolean {
  val firstChar = text.firstOrNull()
  return firstChar?.isLetter() == true || firstChar == UNDERSCORE
}
