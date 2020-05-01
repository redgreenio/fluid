package io.redgreen.fluid.registry.model

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.registry.assist.RegistrySubject
import io.redgreen.fluid.registry.assist.createRegistryFile
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class RegistryTest {
  companion object {
    // Inlining strings in JSON while using IntelliJ language injection causes IDE error.
    // This can be suppressed in our use case.
    @Suppress("JsonStandardCompliance")
    private const val ARTIFACT_SOME_GENERATOR_PATH = "libs/some-generator.jar"

    @Suppress("JsonStandardCompliance") // Same as above.
    private const val ARTIFACT_SHINY_NEW_GENERATOR_PATH = "libs/shiny-new-generator.jar"
  }

  @TempDir
  internal lateinit var supposedlyUserHomePath: Path

  private val registry by lazy { Registry.from(supposedlyUserHomePath) }

  @Language("JSON")
  private val singleEntryRegistryContents = """
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
  fun `it can resolve a path to the fluid home directory`() {
    // when
    val registry = Registry.from(supposedlyUserHomePath)

    // then
    val expectedPath = supposedlyUserHomePath.resolve(".fluid")
    assertThat(registry.root)
      .isEqualTo(expectedPath)
  }

  @Test
  fun `it should create a new file and add an entry if the file does not exist`() {
    // precondition
    RegistrySubject.assertThat(registry)
      .registryFileExists(false)

    // given
    val registryEntry = RegistryEntry("generator-id", ARTIFACT_SOME_GENERATOR_PATH)

    // when
    registry.add(registryEntry)

    // then
    RegistrySubject.assertThat(registry)
      .registryFileContentsEqual(singleEntryRegistryContents)
  }

  @Test
  fun `it should append an existing entry to an already existing registry file`() {
    // precondition
    registry.createRegistryFile(singleEntryRegistryContents)
    RegistrySubject.assertThat(registry)
      .registryFileContentsEqual(singleEntryRegistryContents)

    // given
    val registryEntry = RegistryEntry("generator-id", ARTIFACT_SHINY_NEW_GENERATOR_PATH)

    // when
    registry.add(registryEntry)

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

    RegistrySubject.assertThat(registry)
      .registryFileContentsEqual(updatedRegistryFileContents)
  }

  @Test
  fun `it should replace a corrupt registry and add an entry`() {
    // precondition
    val corruptRegistryContents = " /* Representation of a corrupt registry file */ "
    registry.createRegistryFile(corruptRegistryContents)
    RegistrySubject.assertThat(registry)
      .registryFileContentsEqual(corruptRegistryContents)

    // given
    val registryEntry = RegistryEntry("generator-id", ARTIFACT_SOME_GENERATOR_PATH)

    // when
    registry.add(registryEntry)

    // then
    RegistrySubject.assertThat(registry)
      .registryFileContentsEqual(singleEntryRegistryContents)
  }

  @Language("JSON")
  private val multipleEntriesRegistryContents = """
    {
        "entries": [
          {
            "id": "some-generator-id",
            "artifactName": "some-artifact-name.jar"
          },
          {
            "id": "other-generator-id",
            "artifactName": "some-other-name.jar"
          }
        ]
      }
    """.trimIndent()

  @Test
  fun `it should update an existing entry in the registry`() {
    // precondition
    registry.createRegistryFile(multipleEntriesRegistryContents)
    RegistrySubject.assertThat(registry)
      .registryFileContentsEqual(multipleEntriesRegistryContents)

    // given
    val updatedRegistryEntry = RegistryEntry("other-generator-id", "some-updated-artifact-name.jar")

    // when
    registry.update(updatedRegistryEntry)

    // then
    @Language("JSON")
    val updatedRegistryFileContents = """
      {
        "entries": [
          {
            "id": "some-generator-id",
            "artifactName": "some-artifact-name.jar"
          },
          {
            "id": "other-generator-id",
            "artifactName": "some-updated-artifact-name.jar"
          }
        ]
      }
    """.trimIndent()

    RegistrySubject.assertThat(registry)
      .registryFileContentsEqual(updatedRegistryFileContents)
  }
}
