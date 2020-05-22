package io.redgreen.fluid.prompts

import com.google.common.truth.Truth.assertThat
import io.redgreen.ask.ValidationResult.Failure
import io.redgreen.ask.ValidationResult.Success
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ValidatorsTest {
  @Nested
  inner class KebabCase {
    @ParameterizedTest
    @ValueSource(strings = [
      "",
      "1234567",
      "9-oh-clock",
      "-",
      "-oh-clock",
      "oh-clock-",
      "-clock-",
      "oh-w%-know",
      "${'$'}million"
    ])
    fun `it should return validation failure for invalid text`(text: String) {
      val validationResult = KEBAB_CASE(text)

      val validationFailedMessage =
        "Should start with an alphabet and can contain alphabets (lowercase), numbers, and hyphens (-)."
      assertThat(validationResult)
        .isEqualTo(Failure(text, validationFailedMessage))
    }

    @ParameterizedTest
    @ValueSource(strings = [
      "a",
      "paper",
      "paper-mache",
      "a99",
      "retrofit-rxjava-adapter"
    ])
    fun `it should return validation success for valid text`(text: String) {
      val validationResult = KEBAB_CASE(text)

      assertThat(validationResult)
        .isEqualTo(Success(text))
    }
  }

  @Nested
  inner class PackageName {
    @ParameterizedTest
    @ValueSource(strings = [
      "io",
      "io.redgreen",
      "io.redgreen.fluid",
      "com.mobs_geeks.saripaar",
      "io.cloud9.what",
      "org.example.hyphenated_name",
      "com.example._123name"
    ])
    fun `it should return validation success for valid package names`(packageName: String) {
      val validationResult = PACKAGE_NAME(packageName)

      assertThat(validationResult)
        .isEqualTo(Success(packageName))
    }

    @ParameterizedTest
    @ValueSource(strings = [
      "org.example.hyphenated-name",
      "org.example.million$",
      "123.example.com",
      "com.99times.hello",
      "com.example.123name",
      "org..example",
      "org.example.",
      "..."
    ])
    fun `it should return validation failure for invalid package names`(packageName: String) {
      val validationResult = PACKAGE_NAME(packageName)

      val message = "Can contain alphabets, numbers (preceded by an alphabet or underscore)," +
        " underscores (_) and dots."
      assertThat(validationResult)
        .isEqualTo(Failure(packageName, message))
    }
  }
}
