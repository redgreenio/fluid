package io.redgreen.fluid.engine.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.assist.getTestJarArtifact
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.ValidGenerator
import io.redgreen.fluid.engine.model.DirectoryCreated
import io.redgreen.fluid.engine.model.FileCreated
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class RunGeneratorUseCaseTest {
  @TempDir
  lateinit var destinationDir: File

  @Test
  fun `it should run a valid generator`() {
    // given
    val generatorJarPath = getTestJarArtifact("valid-generator.jar")
    val validGenerator = ValidateGeneratorJarUseCase().invoke(generatorJarPath) as ValidGenerator
    val generatorClass = validGenerator.generatorClass
      .asSubclass(Generator::class.java)

    // when
    val result = RunGeneratorUseCase().invoke(generatorClass, destinationDir)

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
}
