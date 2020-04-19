package io.redgreen.fluid.registry.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.assist.moshi
import io.redgreen.fluid.registry.assist.RegistrySubject.Companion.assertThat
import io.redgreen.fluid.registry.assist.createRegistryFile
import io.redgreen.fluid.registry.domain.AddRegistryEntryUseCase.Result.EntryAdded
import io.redgreen.fluid.registry.model.Registry
import io.redgreen.fluid.registry.model.RegistryEntry
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class AddRegistryEntryUseCaseTest {
  companion object {
    // Language injection inlining causes IDE error, however this can be suppressed for our use case.
    @Suppress("JsonStandardCompliance")
    private const val ARTIFACT_SOME_GENERATOR_PATH = "libs/some-generator.jar"

    @Suppress("JsonStandardCompliance") // Same as above.
    private const val ARTIFACT_SHINY_NEW_GENERATOR_PATH = "libs/shiny-new-generator.jar"
  }

  @TempDir
  internal lateinit var supposedlyUserHomeDir: Path

  private val registry by lazy {
    Registry.from(supposedlyUserHomeDir)
  }

  private val useCase by lazy {
    AddRegistryEntryUseCase(registry, moshi)
  }

  @Language("JSON")
  private val registryFileContents = """
    {
      "entries": [
        {
          "id": "generator-id",
          "artifactName": "$ARTIFACT_SOME_GENERATOR_PATH"
        }
      ]
    }
    """.trimIndent()

  @Test
  fun `it should create a new file and add an entry if the file does not exist`() {
    // precondition
    assertThat(registry)
      .registryFileExists(false)

    // given
    val registryEntry = RegistryEntry("generator-id", ARTIFACT_SOME_GENERATOR_PATH)

    // when
    val result = useCase.invoke(registryEntry)

    // then
    assertThat(result)
      .isEqualTo(EntryAdded)
    assertThat(registry)
      .registryFileContentsEqual(registryFileContents)
  }

  @Test
  fun `it should append an existing entry to an already existing registry file`() {
    // precondition
    registry.createRegistryFile(registryFileContents)
    assertThat(registry)
      .registryFileContentsEqual(registryFileContents)

    // given
    val registryEntry = RegistryEntry("generator-id", ARTIFACT_SHINY_NEW_GENERATOR_PATH)

    // when
    val result = useCase.invoke(registryEntry)

    // then
    @Language("JSON") // FIXME "id" is "generator-id" for both entries
    val updatedRegistryFileContents = """
      {
        "entries": [
          {
            "id": "generator-id",
            "artifactName": "$ARTIFACT_SOME_GENERATOR_PATH"
          },
          {
            "id": "generator-id",
            "artifactName": "$ARTIFACT_SHINY_NEW_GENERATOR_PATH"
          }
        ]
      }
    """.trimIndent()

    assertThat(result)
      .isEqualTo(EntryAdded)
    assertThat(registry)
      .registryFileContentsEqual(updatedRegistryFileContents)
  }

  @Test
  fun `it should replace a corrupt registry and add an entry`() {
    // precondition
    val corruptRegistryContents = " /* Representation of a corrupt registry file */ "
    registry.createRegistryFile(corruptRegistryContents)
    assertThat(registry)
      .registryFileContentsEqual(corruptRegistryContents)

    // given
    val registryEntry = RegistryEntry("generator-id", ARTIFACT_SOME_GENERATOR_PATH)

    // when
    val result = useCase.invoke(registryEntry)

    // then
    assertThat(result)
      .isEqualTo(EntryAdded)
    assertThat(registry)
      .registryFileContentsEqual(registryFileContents)
  }
}
