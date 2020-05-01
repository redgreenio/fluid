package io.redgreen.fluid.registry.domain

import io.redgreen.fluid.registry.assist.RegistrySubject.Companion.assertThat
import io.redgreen.fluid.registry.assist.createRegistryFile
import io.redgreen.fluid.registry.model.Registry
import io.redgreen.fluid.registry.model.RegistryEntry
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class RegistryUpdateEntryTest {
  @TempDir
  internal lateinit var supposedlyUserHomeDir: Path

  private val registry by lazy {
    Registry.from(supposedlyUserHomeDir)
  }

  @Language("JSON")
  private val registryFileContents = """
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
    registry.createRegistryFile(registryFileContents)
    assertThat(registry)
      .registryFileContentsEqual(registryFileContents)

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

    assertThat(registry)
      .registryFileContentsEqual(updatedRegistryFileContents)
  }
}
