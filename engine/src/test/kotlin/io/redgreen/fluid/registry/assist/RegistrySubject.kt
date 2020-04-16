package io.redgreen.fluid.registry.assist

import com.google.common.truth.Fact.simpleFact
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.registry.model.Registry
import java.nio.file.Files

class RegistrySubject(
  metadata: FailureMetadata,
  private val actual: Registry
) : Subject(metadata, actual) {
  companion object {
    private val registrySubjects = Factory(::RegistrySubject)

    @JvmStatic
    fun assertThat(registry: Registry): RegistrySubject =
      assertAbout(registrySubjects).that(registry)
  }

  fun registryFileContentsEqual(expected: String) {
    assertThat(actual.registryManifestPath.toFile().readText())
      .isEqualTo(expected)
  }

  fun registryFileExists(expected: Boolean) {
    assertThat(Files.exists(actual.registryManifestPath))
      .isEqualTo(expected)
  }

  fun containsArtifact(artifactName: String) {
    val artifactPath = actual.artifactPath(artifactName)
    if (!Files.exists(artifactPath)) {
      failWithoutActual(
        simpleFact("expected: a file '$artifactName' at '${artifactPath}'"),
        simpleFact("but was : not found")
      )
    }
  }
}
