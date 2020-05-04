package io.redgreen.fluid.engine.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.assist.ARTIFACT_VALID_GENERATOR
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.InstallationType.FRESH
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.InstallationType.OVERWRITE
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.Result.FreshInstallSuccessful
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.Result.OverwriteSuccessful
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.DefaultRegistry
import io.redgreen.fluid.registry.assist.ValidGeneratorParameterResolver
import io.redgreen.fluid.registry.assist.ValidGeneratorParameterResolver.TestArtifact
import io.redgreen.fluid.registry.model.RegistryEntry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

@ExtendWith(ValidGeneratorParameterResolver::class)
class InstallGeneratorUseCaseTest {
  @TempDir
  internal lateinit var supposedlyUserHomeDir: Path

  private val registry by lazy { DefaultRegistry.from(supposedlyUserHomeDir) }
  private val installGeneratorUseCase by lazy { InstallGeneratorUseCase(registry) }

  @Test
  fun `it should add an entry to the registry after installation`( // FIXME This test is not verifying if a registry entry was added.
    @TestArtifact(ARTIFACT_VALID_GENERATOR) candidate: ValidGenerator
  ) {
    // when
    val result = installGeneratorUseCase.invoke(candidate, FRESH) as FreshInstallSuccessful

    // then
    val freshInstallSuccessful = FreshInstallSuccessful(RegistryEntry("generator-id", ARTIFACT_VALID_GENERATOR))
    assertThat(result)
      .isEqualTo(freshInstallSuccessful)
    val installedArtifactExists = Files.exists(registry.artifactsPath.resolve(ARTIFACT_VALID_GENERATOR))
    assertThat(installedArtifactExists)
      .isTrue()
  }

  @Test // FIXME This test is not verifying if a registry entry was updated.
  fun `it should update an entry in the registry after overwriting an already installed generator`(
    @TestArtifact(ARTIFACT_VALID_GENERATOR) installed: ValidGenerator,
    @TestArtifact("valid-generator-different-sha256.jar") candidate: ValidGenerator
  ) {
    // given
    val freshInstallResult = installGeneratorUseCase.invoke(installed, FRESH)
    assertThat(freshInstallResult)
      .isEqualTo(FreshInstallSuccessful(RegistryEntry("generator-id", ARTIFACT_VALID_GENERATOR)))

    // when
    val result = installGeneratorUseCase.invoke(candidate, OVERWRITE)

    // then
    val overwriteSuccessful = OverwriteSuccessful(RegistryEntry("generator-id", "valid-generator-different-sha256.jar"))
    assertThat(result)
      .isEqualTo(overwriteSuccessful)

    val installedArtifactDoesNotExist = Files.notExists(registry.artifactsPath.resolve(ARTIFACT_VALID_GENERATOR))
    assertThat(installedArtifactDoesNotExist)
      .isTrue()

    val overwrittenArtifactExists = Files.exists(registry.artifactsPath.resolve("valid-generator-different-sha256.jar"))
    assertThat(overwrittenArtifactExists)
      .isTrue()
  }
}
