package io.redgreen.fluid.registry.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.assist.ARTIFACT_VALID_GENERATOR
import io.redgreen.fluid.engine.model.GeneratorJar
import io.redgreen.fluid.registry.assist.GeneratorJarParameterResolver
import io.redgreen.fluid.registry.assist.RegistryHomeSubject.Companion.assertThat
import io.redgreen.fluid.registry.domain.CopyGeneratorUseCase.Result.GeneratorCopied
import io.redgreen.fluid.registry.model.RegistryHome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

@ExtendWith(GeneratorJarParameterResolver::class)
class CopyGeneratorUseCaseTest {
  @TempDir
  internal lateinit var supposedlyUserHomeDir: Path

  private val registryHome by lazy {
    RegistryHome.from(supposedlyUserHomeDir)
  }

  @Test
  fun `it should copy generator jars inside the registry's libs directory`(
    generatorJar: GeneratorJar
  ) {
    // given
    val useCase = CopyGeneratorUseCase(registryHome)

    // when
    val result = useCase.invoke(generatorJar)

    // then
    val destinationPath = registryHome.artifactPath(ARTIFACT_VALID_GENERATOR)

    assertThat(result)
      .isEqualTo(GeneratorCopied(destinationPath))
    assertThat(registryHome)
      .containsArtifact(ARTIFACT_VALID_GENERATOR)
  }
}
