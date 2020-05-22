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
    private val validationFailedMessage =
      "Should start with an alphabet and can contain alphabets (lowercase), numbers, and hyphens (-)."

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
}
