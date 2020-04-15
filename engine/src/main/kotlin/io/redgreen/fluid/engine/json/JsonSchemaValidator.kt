package io.redgreen.fluid.engine.json

import org.everit.json.schema.Schema
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject

class JsonSchemaValidator private constructor(
  private val schema: Schema
) {
  companion object {
    fun forSchema(schemaJsonObject: JSONObject): JsonSchemaValidator =
      JsonSchemaValidator(createSchema(schemaJsonObject))

    private fun createSchema(schema: JSONObject): Schema {
      return SchemaLoader
        .builder()
        .schemaJson(schema)
        .build()
        .load()
        .build()
    }
  }

  fun validate(documentJsonObject: JSONObject): List<Violation> {
    return try {
      schema.validate(documentJsonObject)
      emptyList()
    } catch (exception: ValidationException) {
      if (exception.violationCount > 1) {
        exception.causingExceptions.map(::createViolation)
      } else {
        listOf(createViolation(exception))
      }
    }
  }

  private fun createViolation(exception: ValidationException) =
    Violation(humanize(exception.pointerToViolation), exception.errorMessage)

  private fun humanize(pointerToViolation: String): String =
    pointerToViolation.drop(2) // Dropping chars #/ from the pointer
}
