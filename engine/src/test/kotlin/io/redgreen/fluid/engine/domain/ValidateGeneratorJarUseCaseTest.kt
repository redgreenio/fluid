package io.redgreen.fluid.engine.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.assist.getTestArtifact
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.DoesNotImplementGeneratorInterface
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.JarNotFound
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.MissingManifestAttributes
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.MissingDefaultConstructor
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.MissingGeneratorClassSpecifiedInManifest
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.NotGeneratorJar
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.ValidGenerator
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class ValidateGeneratorJarUseCaseTest {
  private val useCase = ValidateGeneratorJarUseCase()

  @Test
  fun `it should return file does not exists for a non-existent location`() {
    // given
    val nonExistentPath = Paths.get("non-existent.jar")

    // when
    val result = useCase.invoke(nonExistentPath)

    // then
    assertThat(result)
      .isEqualTo(JarNotFound(nonExistentPath))
  }

  @Test
  fun `it should return invalid generator jar if the file path points to file that is not a jar`() {
    // given
    val notJarPath = getTestArtifact("not-a-jar.jar")

    // when
    val result = useCase.invoke(notJarPath)

    // then
    assertThat(result)
      .isEqualTo(NotGeneratorJar(notJarPath))
  }

  @Test
  fun `it should return missing generator attribute if the manifest does not contain the generator attribute`() {
    // given
    val artifactWithMissingAttributesPath = getTestArtifact("missing-generator-attribute.jar")

    // when
    val result = useCase.invoke(artifactWithMissingAttributesPath)

    // then
    assertThat(result)
      .isEqualTo(MissingManifestAttributes(artifactWithMissingAttributesPath))
  }

  @Test
  fun `it should return a valid generator result if the manifest contains a valid generator class`() {
    // given
    val validArtifactPath = getTestArtifact("valid-generator.jar")

    // when
    val result = useCase.invoke(validArtifactPath) as ValidGenerator

    // then
    assertThat(result.artifactPath)
      .isEqualTo(validArtifactPath)
    assertThat(result.generatorClass.name)
      .isEqualTo("com.example.generator.LibraryProjectGenerator")
  }

  @Test
  fun `it should return missing generator class if the class specified in the manifest is missing in the jar`() {
    // given
    val artifactWithMissingGeneratorClassPath = getTestArtifact("missing-generator-class.jar")
    val missingClassName = "com.example.generator.MissingGeneratorClass"

    // when
    val result = useCase.invoke(artifactWithMissingGeneratorClassPath)

    // then
    assertThat(result)
      .isEqualTo(MissingGeneratorClassSpecifiedInManifest(artifactWithMissingGeneratorClassPath, missingClassName))
  }

  @Test
  fun `it should return unexpected generator type if the class specified in the manifest is not a generator`() {
    // given
    val artifactWithUnexpectedGeneratorTypePath = getTestArtifact("unexpected-generator-type.jar")
    val unexpectedClassName = "com.example.generator.LibraryProjectConfig"

    // when
    val result = useCase.invoke(artifactWithUnexpectedGeneratorTypePath)

    // then
    assertThat(result)
      .isEqualTo(DoesNotImplementGeneratorInterface(artifactWithUnexpectedGeneratorTypePath, unexpectedClassName))
  }

  @Test
  fun `it should return missing default constructor if the generator does not have a default constructor`() {
    // given
    val artifactWithMissingDefaultConstructorPath = getTestArtifact("generator-no-default-constructor.jar")
    val generatorClassName = "com.example.generator.LibraryProjectGenerator"

    // when
    val result = useCase.invoke(artifactWithMissingDefaultConstructorPath)

    // then
    assertThat(result)
      .isEqualTo(MissingDefaultConstructor(artifactWithMissingDefaultConstructorPath, generatorClassName))
  }
}
