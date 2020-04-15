package io.redgreen.fluid.engine.json

import com.google.common.truth.Truth.assertThat
import org.intellij.lang.annotations.Language
import org.json.JSONObject
import org.junit.jupiter.api.Test

class JsonSchemaValidatorTest {
  @Language("JSON")
  private val rectangleSchema = """
    {
      "${'$'}schema": "http://json-schema.org/draft-07/schema",
      "type": "object",
      "properties": {
        "rectangle": {
          "${'$'}ref": "#/definitions/Rectangle"
        }
      },
      "definitions": {
        "size": {
          "type": "number",
          "minimum": 0
        },
        "Rectangle": {
          "type": "object",
          "properties": {
            "width": {
              "${'$'}ref": "#/definitions/size"
            },
            "height": {
              "${'$'}ref": "#/definitions/size"
            }
          },
          "required": ["width", "height"]
        }
      }
    }
  """.trimIndent()

  private val validator = JsonSchemaValidator.forSchema(JSONObject(rectangleSchema))

  @Test
  fun `it should return an empty validation errors list for a valid JSON document`() {
    // given
    @Language("JSON")
    val validDocument = """
      {
        "rectangle": {
          "width": 5,
          "height": 5
        }
      }
    """.trimIndent()

    // when
    val violations = validator.validate(JSONObject(validDocument))

    // then
    assertThat(violations)
      .isEmpty()
  }

  @Test
  fun `it should return a violation when there is a violation`() {
    // given
    @Language("JSON")
    val missingWidthRectangle = """
      {
        "rectangle": {
          "height": 5
        }
      }
    """.trimIndent()

    // when
    val violations = validator.validate(JSONObject(missingWidthRectangle))

    // then
    assertThat(violations)
      .containsExactly(
        Violation("rectangle", "required key [width] not found")
      )
  }

  @Test
  fun `it should return multiple violations when there are more than 1 violations`() {
    // given
    @Language("JSON")
    val missingWidthAndHeightRectangle = """
      {
        "rectangle": {
        }
      }
    """.trimIndent()

    // when
    val violations = validator.validate(JSONObject(missingWidthAndHeightRectangle))

    // then
    assertThat(violations)
      .containsExactly(
        Violation("rectangle", "required key [width] not found"),
        Violation("rectangle", "required key [height] not found")
      )
      .inOrder()
  }
}
