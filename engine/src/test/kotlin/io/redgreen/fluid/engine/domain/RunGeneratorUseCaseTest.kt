package io.redgreen.fluid.engine.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.assist.ARTIFACT_VALID_GENERATOR
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.InstallationType.FRESH
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.Result.FreshInstallSuccessful
import io.redgreen.fluid.engine.domain.RunGeneratorUseCase.Result.GeneratorNotFound
import io.redgreen.fluid.engine.domain.RunGeneratorUseCase.Result.RunSuccessful
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.engine.model.DirectoryCreated
import io.redgreen.fluid.engine.model.FileCreated
import io.redgreen.fluid.registry.DefaultRegistry
import io.redgreen.fluid.registry.assist.ValidGeneratorParameterResolver
import io.redgreen.fluid.registry.assist.ValidGeneratorParameterResolver.TestArtifact
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

@ExtendWith(ValidGeneratorParameterResolver::class)
class RunGeneratorUseCaseTest {
  @TempDir
  lateinit var supposedlyUserHomeDir: Path

  @TempDir
  lateinit var destinationPath: Path

  private val registry by lazy { DefaultRegistry.from(supposedlyUserHomeDir) }

  @Test
  fun `it should run a valid generator`(
    @TestArtifact(ARTIFACT_VALID_GENERATOR) candidate: ValidGenerator
  ) {
    // given
    val installSuccessful = InstallGeneratorUseCase(registry)
      .invoke(candidate, FRESH) as FreshInstallSuccessful

    // when
    val result = RunGeneratorUseCase(registry)
      .invoke(installSuccessful.registryEntry.id, destinationPath) as RunSuccessful

    // then
    assertThat(result.realizations)
      .containsExactly(
        DirectoryCreated("src/main/java"),
        DirectoryCreated("src/main/kotlin"),
        DirectoryCreated("src/test/java"),
        DirectoryCreated("src/test/kotlin"),
        FileCreated(".gitignore"),
        FileCreated("build.gradle"),
        FileCreated("icon.png")
      )
  }

  @Test
  fun `it should return not installed if the generator is not installed`() {
    // when
    val result = RunGeneratorUseCase(registry).invoke("non-existent-generator-id", destinationPath)

    // then
    assertThat(result)
      .isEqualTo(GeneratorNotFound)
  }
}
