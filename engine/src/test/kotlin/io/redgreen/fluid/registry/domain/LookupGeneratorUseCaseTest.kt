package io.redgreen.fluid.registry.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.assist.ARTIFACT_VALID_GENERATOR
import io.redgreen.fluid.assist.moshi
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.engine.model.Manifest
import io.redgreen.fluid.registry.assist.ValidGeneratorParameterResolver
import io.redgreen.fluid.registry.assist.ValidGeneratorParameterResolver.TestArtifact
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.AlreadyInstalled
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentHashes
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentVersions
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.NotInstalled
import io.redgreen.fluid.registry.model.Registry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

@ExtendWith(ValidGeneratorParameterResolver::class)
class LookupGeneratorUseCaseTest {
  @TempDir
  internal lateinit var supposedlyUserHomeDir: Path // TODO Remove duplication

  private val registry by lazy {
    Registry.from(supposedlyUserHomeDir)
  }

  private val installGeneratorUseCase by lazy {
    InstallGeneratorUseCase(registry, moshi)
  }

  private val lookupGeneratorUseCase by lazy {
    LookupGeneratorUseCase()
  }

  @Test
  fun `it should return not found for a generator that is not installed`(
    @TestArtifact(ARTIFACT_VALID_GENERATOR) validGenerator: ValidGenerator
  ) {
    // when
    val result = lookupGeneratorUseCase.invoke(registry, validGenerator)

    // then
    assertThat(result)
      .isEqualTo(NotInstalled)
  }

  @Test
  fun `it should return already installed if the artifact's signature is same as the one that's being installed`(
    @TestArtifact(ARTIFACT_VALID_GENERATOR) installedGenerator: ValidGenerator
  ) {
    // given
    installGeneratorUseCase.invoke(installedGenerator)

    // when
    val result = lookupGeneratorUseCase.invoke(registry, installedGenerator)

    // then
    assertThat(result)
      .isEqualTo(AlreadyInstalled)
  }

  @Test
  fun `it should return different SHA-256 if the already installed generator's hash is different`(
    @TestArtifact(ARTIFACT_VALID_GENERATOR) installedGenerator: ValidGenerator,
    @TestArtifact("valid-generator-different-sha256.jar") updatedGenerator: ValidGenerator
  ) {
    // given
    installGeneratorUseCase.invoke(installedGenerator)

    // when
    val result = lookupGeneratorUseCase.invoke(registry, updatedGenerator)

    // then
    assertThat(result)
      .isEqualTo(DifferentHashes(installedGenerator.sha256, updatedGenerator.sha256))
  }

  @Test
  fun `it should return versions differ if the installed version is different from the generator being installed`(
    @TestArtifact(ARTIFACT_VALID_GENERATOR) olderGenerator: ValidGenerator,
    @TestArtifact("valid-generator-newer-version.jar") newerGenerator: ValidGenerator
  ) {
    // given
    installGeneratorUseCase.invoke(olderGenerator)

    // when
    val result = lookupGeneratorUseCase.invoke(registry, newerGenerator)

    // then
    val installedGeneratorVersion = getVersion(olderGenerator.manifest)
    val generatorToInstallVersion = getVersion(newerGenerator.manifest)
    assertThat(result)
      .isEqualTo(DifferentVersions(installedGeneratorVersion, generatorToInstallVersion))
  }

  private fun getVersion(manifest: Manifest): String =
    manifest.generator.version
}
