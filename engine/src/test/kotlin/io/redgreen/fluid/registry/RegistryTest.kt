package io.redgreen.fluid.registry

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.registry.assist.RegistrySubject.Companion.assertThat
import io.redgreen.fluid.registry.assist.createRegistryFile
import io.redgreen.fluid.registry.model.RegistryEntry
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class RegistryTest {
  // Inlining strings in JSON while using IntelliJ language injection causes IDE error.
  // This can be suppressed in our use case.
  @Suppress("JsonStandardCompliance")
  companion object {
    private const val GENERATOR_A_ID = "a-id"
    private const val GENERATOR_A_PATH = "generator-a.jar"

    private const val GENERATOR_B_ID = "b-id"
    private const val GENERATOR_B_PATH = "generator-b.jar"
  }

  @TempDir
  internal lateinit var supposedlyUserHome: Path

  private val registry by lazy { Registry.from(supposedlyUserHome) }

  @Test
  fun `it should resolve registry's root directory`() {
    // when
    val registry = Registry.from(supposedlyUserHome)

    // then
    val expectedPath = supposedlyUserHome.resolve(".fluid")
    assertThat(registry.root)
      .isEqualTo(expectedPath)
  }

  @Nested
  inner class AddRegistryEntry {
    @Language("JSON")
    private val registryFileWithGeneratorA = """
        {
          "entries": [
            {
              "id": "$GENERATOR_A_ID",
              "artifactName": "$GENERATOR_A_PATH"
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
      val newEntry = RegistryEntry(GENERATOR_A_ID, GENERATOR_A_PATH)

      // when
      registry.add(newEntry)

      // then
      assertThat(registry)
        .registryFileContentsEqual(registryFileWithGeneratorA)
    }

    @Test
    fun `it should append an entry if the registry file exists`() {
      // precondition
      registry.createRegistryFile(registryFileWithGeneratorA)
      assertThat(registry)
        .registryFileContentsEqual(registryFileWithGeneratorA)

      // given
      val registryEntry = RegistryEntry(GENERATOR_B_ID, GENERATOR_B_PATH)

      // when
      registry.add(registryEntry)

      // then
      @Language("JSON")
      val updatedRegistryFileContents = """
          {
            "entries": [
              {
                "id": "$GENERATOR_A_ID",
                "artifactName": "$GENERATOR_A_PATH"
              },
              {
                "id": "$GENERATOR_B_ID",
                "artifactName": "$GENERATOR_B_PATH"
              }
            ]
          }
        """.trimIndent()

      assertThat(registry)
        .registryFileContentsEqual(updatedRegistryFileContents)
    }

    @Test
    fun `it should replace a corrupt registry and add an entry`() {
      // precondition
      val corruptRegistryContents = "/* Representation of a corrupt registry file */"
      registry.createRegistryFile(corruptRegistryContents)
      assertThat(registry)
        .registryFileContentsEqual(corruptRegistryContents)

      // given
      val registryEntry = RegistryEntry(GENERATOR_A_ID, GENERATOR_A_PATH)

      // when
      registry.add(registryEntry)

      // then
      assertThat(registry)
        .registryFileContentsEqual(registryFileWithGeneratorA)
    }

    @Test
    fun `it should update an entry if the entry already exists`() {
      // precondition
      registry.createRegistryFile(registryFileWithGeneratorA)
      assertThat(registry)
        .registryFileContentsEqual(registryFileWithGeneratorA)

      // given
      val registryEntry = RegistryEntry(GENERATOR_A_ID, "updated-generator-a.jar")

      // when
      registry.add(registryEntry)

      // then
      @Language("JSON")
      val updatedRegistryFileContents = """
          {
            "entries": [
              {
                "id": "$GENERATOR_A_ID",
                "artifactName": "updated-generator-a.jar"
              }
            ]
          }
        """.trimIndent()

      assertThat(registry)
        .registryFileContentsEqual(updatedRegistryFileContents)
    }
  }

  @Nested
  inner class UpdateRegistryEntry {
    @Language("JSON")
    private val registryFileWithGeneratorsAB = """
        {
          "entries": [
            {
              "id": "$GENERATOR_A_ID",
              "artifactName": "$GENERATOR_A_PATH"
            },
            {
              "id": "$GENERATOR_B_ID",
              "artifactName": "$GENERATOR_B_PATH"
            }
          ]
        }
      """.trimIndent()

    @Test
    fun `it should update an existing entry in the registry`() {
      // precondition
      registry.createRegistryFile(registryFileWithGeneratorsAB)
      assertThat(registry)
        .registryFileContentsEqual(registryFileWithGeneratorsAB)

      // given
      val updatedRegistryEntry = RegistryEntry(GENERATOR_B_ID, "updated-generator-b.jar")

      // when
      registry.update(updatedRegistryEntry)

      // then
      @Language("JSON")
      val updatedRegistryFileContents = """
          {
            "entries": [
              {
                "id": "$GENERATOR_A_ID",
                "artifactName": "$GENERATOR_A_PATH"
              },
              {
                "id": "$GENERATOR_B_ID",
                "artifactName": "updated-generator-b.jar"
              }
            ]
          }
        """.trimIndent()

      assertThat(registry)
        .registryFileContentsEqual(updatedRegistryFileContents)
    }
  }
}
