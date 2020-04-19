package io.redgreen.fluid.registry.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.assist.ARTIFACT_VALID_GENERATOR
import io.redgreen.fluid.assist.moshi
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.assist.ValidGeneratorParameterResolver
import io.redgreen.fluid.registry.assist.ValidGeneratorParameterResolver.TestArtifact
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase.InstallationType.FRESH
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase.Result.FreshInstallSuccessful
import io.redgreen.fluid.registry.model.Registry
import io.redgreen.fluid.registry.model.RegistryEntry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

@ExtendWith(ValidGeneratorParameterResolver::class)
class InstallGeneratorUseCaseTest {
  @TempDir
  internal lateinit var supposedlyUserHomeDir: Path

  private val registry by lazy {
    Registry.from(supposedlyUserHomeDir)
  }

  @Test
  fun `it should add an entry to the registry after installation`(
    @TestArtifact(ARTIFACT_VALID_GENERATOR) validGenerator: ValidGenerator
  ) {
    // given
    val useCase = InstallGeneratorUseCase(registry, moshi)

    // when
    val result = useCase.invoke(validGenerator, FRESH) as FreshInstallSuccessful

    // then
    assertThat(result)
      .isEqualTo(
        FreshInstallSuccessful(RegistryEntry("generator-id", ARTIFACT_VALID_GENERATOR))
      )
  }
}
