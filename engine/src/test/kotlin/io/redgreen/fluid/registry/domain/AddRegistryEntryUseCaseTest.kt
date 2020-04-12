package io.redgreen.fluid.registry.domain

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import io.redgreen.fluid.registry.domain.AddRegistryEntryUseCase.Result.EntryAdded
import io.redgreen.fluid.registry.model.RegistryEntry
import io.redgreen.fluid.registry.model.RegistryHome
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class AddRegistryEntryUseCaseTest {
  @TempDir
  internal lateinit var supposedlyUserHomeDir: Path // TODO Rename other classes to use a similar name

  private val registryHome by lazy {
    RegistryHome.from(supposedlyUserHomeDir)
  }

  private val registryFile by lazy {
    registryHome.path
      .resolve("registry.json")
      .toFile()
  }

  private val moshi by lazy {
    Moshi
      .Builder()
      .build()
  }

  private val useCase by lazy {
    AddRegistryEntryUseCase(registryHome, moshi)
  }

  @Language("JSON")
  val registryFileContents = """
    {
      "entries": [
        {
          "generatorPath": "libs/some-generator.jar"
        }
      ]
    }
    """.trimIndent()

  @Test
  fun `it should create a new file and add an entry if the file does not exist`() {
    // precondition
    registryFileDoesNotExist()

    // given
    val registryEntry = RegistryEntry("libs/some-generator.jar")

    // when
    val result = useCase.invoke(registryEntry)

    // then
    assertThat(result)
      .isEqualTo(EntryAdded)
    assertThat(registryFile.readText())
      .isEqualTo(registryFileContents)
  }

  @Test
  fun `it should append an existing entry to an already existing registry file`() {
    // precondition
    createRegistryFile(registryFileContents)
    registryFileExists(registryFile.readText())

    // given
    val registryEntry = RegistryEntry("libs/shiny-new-generator.jar")

    // when
    val result = useCase.invoke(registryEntry)

    // then
    @Language("JSON")
    val updatedRegistryFileContents = """
      {
        "entries": [
          {
            "generatorPath": "libs/some-generator.jar"
          },
          {
            "generatorPath": "libs/shiny-new-generator.jar"
          }
        ]
      }
    """.trimIndent()

    assertThat(result)
      .isEqualTo(EntryAdded)
    assertThat(registryFile.readText())
      .isEqualTo(updatedRegistryFileContents)
  }

  @Test
  fun `it should replace a corrupt registry and add an entry`() {
    // precondition
    val corruptRegistryContents = " /* Representation of a corrupt registry file */ "
    createRegistryFile(corruptRegistryContents)
    registryFileExists(corruptRegistryContents)

    // given
    val registryEntry = RegistryEntry("libs/some-generator.jar")

    // when
    val result = useCase.invoke(registryEntry)

    // then
    assertThat(result)
      .isEqualTo(EntryAdded)
    assertThat(registryFile.readText())
      .isEqualTo(registryFileContents)
  }

  private fun registryFileDoesNotExist() {
    assertThat(registryFile.exists())
      .isFalse()
  }

  private fun createRegistryFile(contents: String) {
    registryFile.parentFile.mkdirs()
    registryFile.writeText(contents)
  }

  private fun registryFileExists(contents: String) {
    assertThat(registryFile.exists())
      .isTrue()
    assertThat(contents)
      .isEqualTo(registryFile.readText())
  }
}
