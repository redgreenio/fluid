package io.redgreen.fluid.engine.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.DoesNotImplementGeneratorInterface
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.JarNotFound
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.ManifestMissingGeneratorAttribute
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.MissingDefaultConstructor
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.MissingGeneratorClassSpecifiedInManifest
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.NotGeneratorJar
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.ValidGenerator
import org.junit.jupiter.api.Test
import java.io.File

class LoadGeneratorJarUseCaseTest {
  companion object {
    private const val PATH_JAR_TEST_ARTIFACTS = "src/test/resources/jar-test-artifacts"
  }

  private val useCase = LoadGeneratorJarUseCase()

  @Test
  fun `it should return file does not exists for a non-existent location`() {
    // given
    val nonExistentJarPath = "non-existent.jar"

    // when
    val result = useCase.invoke(nonExistentJarPath)

    // then
    assertThat(result)
      .isEqualTo(JarNotFound(nonExistentJarPath))
  }

  @Test
  fun `it should return invalid generator jar if the file path points to file that is not a jar`() {
    // given
    val notJarPath = getJarTestArtifact("not-a-jar.jar")

    // when
    val result = useCase.invoke(notJarPath)

    // then
    assertThat(result)
      .isEqualTo(NotGeneratorJar(notJarPath))
  }

  @Test
  fun `it should return missing generator attribute if the manifest does not contain the generator attribute`() {
    // given
    val missingAttributeJarPath = getJarTestArtifact("missing-generator-attribute.jar")

    // when
    val result = useCase.invoke(missingAttributeJarPath)

    // then
    assertThat(result)
      .isEqualTo(ManifestMissingGeneratorAttribute(missingAttributeJarPath))
  }

  @Test
  fun `it should return a valid generator result if the manifest contains a valid generator class`() {
    // given
    val validGeneratorJarPath = getJarTestArtifact("valid-generator.jar")

    // when
    val result = useCase.invoke(validGeneratorJarPath) as ValidGenerator

    // then
    assertThat(result.jarPath)
      .isEqualTo(validGeneratorJarPath)
    assertThat(result.generatorClass.name)
      .isEqualTo("com.example.generator.LibraryProjectGenerator")
  }

  @Test
  fun `it should return missing generator class if the class specified in the manifest is missing in the jar`() {
    // given
    val missingGeneratorClassJarPath = getJarTestArtifact("missing-generator-class.jar")
    val missingClassName = "com.example.generator.MissingGeneratorClass"

    // when
    val result = useCase.invoke(missingGeneratorClassJarPath)

    // then
    assertThat(result)
      .isEqualTo(MissingGeneratorClassSpecifiedInManifest(missingGeneratorClassJarPath, missingClassName))
  }

  @Test
  fun `it should return unexpected generator type if the class specified in the manifest is not a generator`() {
    // given
    val unexpectedGeneratorTypeJarPath = getJarTestArtifact("unexpected-generator-type.jar")
    val unexpectedClassName = "com.example.generator.LibraryProjectConfig"

    // when
    val result = useCase.invoke(unexpectedGeneratorTypeJarPath)

    // then
    assertThat(result)
      .isEqualTo(DoesNotImplementGeneratorInterface(unexpectedGeneratorTypeJarPath, unexpectedClassName))
  }

  @Test
  fun `it should return missing default constructor if the generator does not have a default constructor`() {
    // given
    val missingDefaultConstructorJarPath = getJarTestArtifact("generator-no-default-constructor.jar")
    val generatorClassName = "com.example.generator.LibraryProjectGenerator"

    // when
    val result = useCase.invoke(missingDefaultConstructorJarPath)

    // then
    assertThat(result)
      .isEqualTo(MissingDefaultConstructor(missingDefaultConstructorJarPath, generatorClassName))
  }

  private fun getJarTestArtifact(artifactFileName: String): String =
    File("")
      .resolve(PATH_JAR_TEST_ARTIFACTS)
      .resolve(artifactFileName)
      .absolutePath
}
