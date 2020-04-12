package io.redgreen.fluid.registry.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.assist.ARTIFACT_VALID_GENERATOR
import io.redgreen.fluid.assist.moshi
import io.redgreen.fluid.engine.model.GeneratorJar
import io.redgreen.fluid.registry.assist.GeneratorJarParameterResolver
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase.Result.GeneratorInstalled
import io.redgreen.fluid.registry.model.RegistryEntry
import io.redgreen.fluid.registry.model.RegistryHome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

@ExtendWith(GeneratorJarParameterResolver::class)
class InstallGeneratorUseCaseTest {
  @TempDir
  internal lateinit var supposedlyUserHomeDir: Path

  private val registryHome by lazy {
    RegistryHome.from(supposedlyUserHomeDir)
  }

  @Test
  fun `it should add an entry to the registry after installation`(
    generatorJar: GeneratorJar
  ) {
    // given
    val useCase = InstallGeneratorUseCase(registryHome, moshi)

    // when
    val result = useCase.invoke(generatorJar)

    // then
    assertThat(result)
      .isEqualTo(
        GeneratorInstalled(RegistryEntry("libs/$ARTIFACT_VALID_GENERATOR"))
      )
  }
}
