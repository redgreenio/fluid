package io.redgreen.fluid.registry.assist

import com.google.common.truth.Fact.simpleFact
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.registry.model.RegistryHome
import java.nio.file.Files

class RegistryHomeSubject(
  metadata: FailureMetadata,
  private val actual: RegistryHome
) : Subject(metadata, actual) {
  companion object {
    private val registryHomeSubjects = Factory(::RegistryHomeSubject)

    @JvmStatic
    fun assertThat(registryHome: RegistryHome): RegistryHomeSubject =
      assertAbout(registryHomeSubjects).that(registryHome)
  }

  fun registryFileContentsEqual(expected: String) {
    assertThat(actual.registryFilePath.toFile().readText())
      .isEqualTo(expected)
  }

  fun registryFileExists(expected: Boolean) {
    assertThat(Files.exists(actual.registryFilePath))
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
