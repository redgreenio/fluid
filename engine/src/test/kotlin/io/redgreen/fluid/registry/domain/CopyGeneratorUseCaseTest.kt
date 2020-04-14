package io.redgreen.fluid.registry.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.assist.ARTIFACT_VALID_GENERATOR
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.assist.RegistryHomeSubject.Companion.assertThat
import io.redgreen.fluid.registry.assist.ValidGeneratorParameterResolver
import io.redgreen.fluid.registry.domain.CopyGeneratorUseCase.Result.GeneratorCopied
import io.redgreen.fluid.registry.model.RegistryHome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

@ExtendWith(ValidGeneratorParameterResolver::class)
class CopyGeneratorUseCaseTest {
  @TempDir
  internal lateinit var supposedlyUserHomeDir: Path

  private val registryHome by lazy {
    RegistryHome.from(supposedlyUserHomeDir)
  }

  @Test
  fun `it should copy generator jars inside the registry's libs directory`(
    validGenerator: ValidGenerator
  ) {
    // given
    val useCase = CopyGeneratorUseCase(registryHome)

    // when
    val result = useCase.invoke(validGenerator)

    // then
    val destinationPath = registryHome.artifactPath(ARTIFACT_VALID_GENERATOR)

    assertThat(result)
      .isEqualTo(GeneratorCopied(destinationPath))
    assertThat(registryHome)
      .containsArtifact(ARTIFACT_VALID_GENERATOR)
  }
}
