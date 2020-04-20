package io.redgreen.fluid.registry.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.assist.ARTIFACT_VALID_GENERATOR
import io.redgreen.fluid.assist.moshi
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.InstallationType.FRESH
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
    LookupGeneratorUseCase(registry)
  }

  @Test
  fun `it should return not found for a generator that is not installed`(
    @TestArtifact(ARTIFACT_VALID_GENERATOR) candidate: ValidGenerator
  ) {
    // when
    val result = lookupGeneratorUseCase.invoke(candidate)

    // then
    assertThat(result)
      .isEqualTo(NotInstalled)
  }

  @Test
  fun `it should return already installed if the artifact's signature is same as the one that's being installed`(
    @TestArtifact(ARTIFACT_VALID_GENERATOR) installed: ValidGenerator,
    @TestArtifact(ARTIFACT_VALID_GENERATOR) candidate: ValidGenerator
  ) {
    // given
    installGeneratorUseCase.invoke(installed, FRESH)

    // when
    val result = lookupGeneratorUseCase.invoke(candidate)

    // then
    assertThat(result)
      .isEqualTo(AlreadyInstalled)
  }

  @Test
  fun `it should return different SHA-256 if the already installed generator's hash is different`(
    @TestArtifact(ARTIFACT_VALID_GENERATOR) installed: ValidGenerator,
    @TestArtifact("valid-generator-different-sha256.jar") candidate: ValidGenerator
  ) {
    // given
    installGeneratorUseCase.invoke(installed, FRESH)

    // when
    val result = lookupGeneratorUseCase.invoke(candidate)

    // then
    assertThat(result)
      .isEqualTo(DifferentHashes(installed.sha256, candidate.sha256))
  }

  @Test
  fun `it should return versions differ if the installed version is different from the generator being installed`(
    @TestArtifact(ARTIFACT_VALID_GENERATOR) olderInstalled: ValidGenerator,
    @TestArtifact("valid-generator-newer-version.jar") newerCandidate: ValidGenerator
  ) {
    // given
    installGeneratorUseCase.invoke(olderInstalled, FRESH)

    // when
    val result = lookupGeneratorUseCase.invoke(newerCandidate)

    // then
    val installedVersion = getVersion(olderInstalled.manifest)
    val candidateVersion = getVersion(newerCandidate.manifest)
    assertThat(result)
      .isEqualTo(DifferentVersions(installedVersion, candidateVersion))
  }

  private fun getVersion(manifest: Manifest): String =
    manifest.generator.version
}
