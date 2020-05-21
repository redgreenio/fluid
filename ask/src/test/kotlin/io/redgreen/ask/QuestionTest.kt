package io.redgreen.ask

import com.google.common.truth.Truth.assertThat
import io.redgreen.ask.Answer.DefaultText
import io.redgreen.ask.Answer.UserText
import io.redgreen.ask.Answer.ValidationFailure
import io.redgreen.ask.ValidationResult.Failure
import io.redgreen.ask.ValidationResult.Success
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class QuestionTest {
  companion object {
    private const val BLANK_STRING = "  "
    private const val EMPTY_STRING = ""
  }

  @Nested
  inner class BasicInput {
    private val question = TextQuestion(
      "Library name",
      "shallot"
    )

    @Test
    fun `it should allow users to input free-form text`() {
      val answer = ask(question) { "leek" }

      assertThat(answer)
        .isEqualTo(UserText("leek"))
    }

    @Test
    fun `it should return the default value for empty user input`() {
      val answer = ask(question) { EMPTY_STRING }

      assertThat(answer)
        .isEqualTo(DefaultText("shallot"))
    }

    @Test
    fun `it should return the default value for blank user input`() {
      val answer = ask(question) { BLANK_STRING }

      assertThat(answer)
        .isEqualTo(DefaultText("shallot"))
    }

    @Test
    fun `it should return empty string for blank user input when there is no default`() {
      // given
      val question = TextQuestion(
        "Library name"
      )

      // when
      val answer = ask(question) { BLANK_STRING }

      // then
      assertThat(answer)
        .isEqualTo(UserText(EMPTY_STRING))
    }

    @Test
    fun `it should trim user inputs`() {
      val answer = ask(question) { "   wonder  " }

      assertThat(answer)
        .isEqualTo(UserText("wonder"))
    }
  }

  @Nested
  inner class InputValidation {
    private val minLengthValidator: (String) -> ValidationResult = { text ->
      if (text.length > 6) {
        Success(text)
      } else {
        Failure(text, "Should have at least 6 characters.")
      }
    }

    private val question = TextQuestion("Library name", "bell-pepper", minLengthValidator)

    @Test
    fun `it should return a validation error for validation failures`() {
      val answer = ask(question) { "mayday" }

      assertThat(answer)
        .isEqualTo(ValidationFailure("mayday", "Should have at least 6 characters."))
    }

    @Test
    fun `it should return user input when validation succeeds`() {
      val answer = ask(question) { "retrofit" }

      assertThat(answer)
        .isEqualTo(UserText("retrofit"))
    }

    @Test
    fun `it should return default value when the validation succeeds`() {
      val answer = ask(question) { EMPTY_STRING }

      assertThat(answer)
        .isEqualTo(DefaultText("bell-pepper"))
    }
  }
}
