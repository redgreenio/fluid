package io.redgreen.fluid.engine.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.engine.domain.ValidateManifestJsonUseCase.Result.InvalidManifest
import io.redgreen.fluid.engine.domain.ValidateManifestJsonUseCase.Result.MalformedJson
import io.redgreen.fluid.engine.domain.ValidateManifestJsonUseCase.Result.Valid
import io.redgreen.fluid.engine.json.Violation
import io.redgreen.fluid.engine.model.GeneratorEntry
import io.redgreen.fluid.engine.model.MaintainerEntry
import io.redgreen.fluid.engine.model.Manifest
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class ValidateManifestJsonUseCaseTest {
  private val useCase = ValidateManifestJsonUseCase()

  @Test
  fun `it should return a valid manifest for a valid JSON`() {
    // given
    @Language("JSON")
    val manifestJson = """
      {
        "generator": {
          "name": "Name",
          "description": "Description",
          "version": "0.1.0",
          "id": "generator-id",
          "implementation": "com.example.generator.ApplicationProjectGenerator"
        },

        "maintainer": {
          "name": "Acme Inc.,",
          "website": "https://example.com",
          "email": "oss@example.com"
        }
      }
    """.trimIndent()

    // when
    val result = useCase.invoke(manifestJson)

    // then
    val generator = GeneratorEntry(
      "generator-id",
      "com.example.generator.ApplicationProjectGenerator",
      "Name",
      "Description",
      "0.1.0"
    )
    val maintainer = MaintainerEntry("Acme Inc.,", "https://example.com", "oss@example.com")
    val manifest = Manifest(generator, maintainer)
    assertThat(result)
      .isEqualTo(Valid(manifest))
  }

  @Test
  fun `it should return an invalid object for JSON with violations`() {
    // given
    @Language("JSON")
    val manifestJson = """
      {
        "generator": {
          "name": "Name",
          "description": "Description",
          "id": "generator-id",
          "implementation": "com.example.generator.ApplicationProjectGenerator"
        },

        "maintainer": {
          "name": "Acme Inc.,",
          "website": "https://example.com"
        }
      }
    """.trimIndent()

    // when
    val result = useCase.invoke(manifestJson)

    // then
    val violations = listOf(
      Violation("generator", "required key [version] not found"),
      Violation("maintainer", "required key [email] not found")
    )
    assertThat(result)
      .isEqualTo(InvalidManifest(violations))
  }

  @Test
  fun `it should return a malformed json for an invalid JSON document`() {
    val malformedJson = """
      {
        "generator": {
          "name": "Name",
          "description": "Description",
          "id": "generator-id",
          "implementation": "com.example.generator.ApplicationProjectGenerator"

        "maintainer": {
          "name": "Acme Inc.,",
          "website": "https://example.com"
        }
      }
    """.trimIndent()

    // when
    val result = useCase.invoke(malformedJson)

    // then
    assertThat(result)
      .isEqualTo(MalformedJson)
  }
}
