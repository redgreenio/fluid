package io.redgreen.fluid.engine.domain

import com.squareup.moshi.Moshi
import io.redgreen.fluid.engine.domain.ValidateManifestJsonUseCase.Result.InvalidManifest
import io.redgreen.fluid.engine.domain.ValidateManifestJsonUseCase.Result.MalformedJson
import io.redgreen.fluid.engine.domain.ValidateManifestJsonUseCase.Result.Valid
import io.redgreen.fluid.engine.json.JsonSchemaValidator
import io.redgreen.fluid.engine.json.Violation
import io.redgreen.fluid.engine.model.Manifest
import org.json.JSONException
import org.json.JSONObject

class ValidateManifestJsonUseCase {
  companion object {
    private const val MANIFEST_SCHEMA_JSON = "manifest-schema.json"
  }

  private val schemaValidator by lazy {
    createManifestSchemaValidator()
  }

  fun invoke(manifestJson: String): Result {
    val manifestJsonObject = try {
      JSONObject(manifestJson)
    } catch (e: JSONException) {
      e.printStackTrace()
      return MalformedJson
    }

    val violations = schemaValidator.validate(manifestJsonObject)
    return if (violations.isEmpty()) {
      val manifestAdapter = Moshi.Builder().build().adapter(Manifest::class.java)
      @Suppress("UnsafeCallOnNullableType")
      Valid(manifestAdapter.fromJson(manifestJson)!!)
    } else {
      InvalidManifest(violations)
    }
  }

  private fun createManifestSchemaValidator(): JsonSchemaValidator {
    val manifestSchemaJson = this::class.java.classLoader
      .getResourceAsStream(MANIFEST_SCHEMA_JSON)!!
      .reader()
      .readText()
    val schemaJsonObject = JSONObject(manifestSchemaJson)
    return JsonSchemaValidator.forSchema(schemaJsonObject)
  }

  sealed class Result {
    data class Valid(val manifest: Manifest) : Result()
    data class InvalidManifest(val violations: List<Violation>) : Result()
    object MalformedJson : Result()
  }
}
