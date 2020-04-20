package io.redgreen.fluid.cli.internal.view

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.Result.FreshInstallSuccessful
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.Result.OverwriteSuccessful
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.AlreadyInstalled
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentHashes
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentVersions
import io.redgreen.fluid.registry.model.RegistryEntry
import org.junit.jupiter.api.Test

internal class ResultExtensionsTest {
  @Test
  fun `it should return a message for a fresh install`() {
    // given
    val freshInstallSuccessful = FreshInstallSuccessful(RegistryEntry("dropwizard", "dropwizard.jar"))

    // when
    val sha256 = "e06d23a21cb227896cf1c5503d04727841920da07279605787726869c043e618"
    val generatorId = freshInstallSuccessful.registryEntry.id
    val userMessage = freshInstallSuccessful.userMessage(sha256)

    // then
    val message = """
        Digest: sha256:$sha256
        Installed generator: '${generatorId}'
      """.trimIndent()
    assertThat(userMessage)
      .isEqualTo(message)
  }

  @Test
  fun `it should return a message for generator already installed`() {
    // given
    val alreadyInstalled = AlreadyInstalled
    val generatorId = "truth-subject"

    // when
    val userMessage = alreadyInstalled.userMessage(generatorId)

    // then
    val message = """
      Generator '$generatorId' is already installed.
      No changes were made.
    """.trimIndent()
    assertThat(userMessage)
      .isEqualTo(message)
  }

  @Test
  fun `it should return a message for a generator with a different hash`() {
    // given
    val installedHash = "e06d23a21cb227896cf1c5503d04727841920da07279605787726869c043e618"
    val candidateHash = "7841920da07279605787726869c043e618e06d23a21cb227896cf1c5503d0472"
    val differentHashes = DifferentHashes(
      installedHash,
      candidateHash
    )
    val generatorId = "liquibase-migration"
    val version = "1.1.0"

    // when
    val userMessage = differentHashes.userMessage(generatorId, version)

    // then
    val message = """
      Generator '$generatorId', version '$version' is already installed.
      However, the generator you are trying to install has a different hash.
      Installed sha256: $installedHash
      Candidate sha256: $candidateHash
    """.trimIndent()
    assertThat(userMessage)
      .isEqualTo(message)
  }

  @Test
  fun `it should return a message for a generator with a newer version`() {
    // given
    val installed = "1.0.1"
    val candidate = "1.1.0"
    val differentVersions = DifferentVersions(installed, candidate)
    val generatorId = "truth-param-resolver"

    // when
    val userMessage = differentVersions.userMessage(generatorId)

    // then
    val message = """
      Generator '$generatorId', version '$installed' is installed.
      The generator you are trying to install will UPGRADE it to '$candidate'.
    """.trimIndent()
    assertThat(userMessage)
      .isEqualTo(message)
  }

  @Test
  fun `it should return a message for a generator with an older version`() {
    // given
    val installed = "1.1.0"
    val candidate = "1.0.1"
    val differentVersions = DifferentVersions(installed, candidate)
    val generatorId = "truth-param-resolver"

    // when
    val userMessage = differentVersions.userMessage(generatorId)

    // then
    val message = """
      Generator '$generatorId', version '$installed' is installed.
      The generator you are trying to install will DOWNGRADE it to '$candidate'.
    """.trimIndent()
    assertThat(userMessage)
      .isEqualTo(message)
  }

  @Test
  fun `it should return a message for a generator with a version that cannot be determined`() {
    // given
    val installed = "RC4"
    val candidate = "1.0.1"
    val differentVersions = DifferentVersions(installed, candidate)
    val generatorId = "truth-param-resolver"

    // when
    val userMessage = differentVersions.userMessage(generatorId)

    // then
    val message = """
      Generator '$generatorId', version '$installed' is installed.
      The generator you are trying to install will CHANGE it to '$candidate'.
    """.trimIndent()
    assertThat(userMessage)
      .isEqualTo(message)
  }

  @Test
  fun `it should return a message for an overwrite install`() {
    // given
    val overwriteSuccessful = OverwriteSuccessful(RegistryEntry("spring-boot", "spring-boot.jar"))

    // when
    val sha256 = "e06d23a21cb227896cf1c5503d04727841920da07279605787726869c043e618"
    val generatorId = overwriteSuccessful.registryEntry.id
    val userMessage = overwriteSuccessful.userMessage(sha256)

    // then
    val message = """
        Digest: sha256:$sha256
        Generator overwritten: '${generatorId}'
      """.trimIndent()
    assertThat(userMessage)
      .isEqualTo(message)
  }
}
