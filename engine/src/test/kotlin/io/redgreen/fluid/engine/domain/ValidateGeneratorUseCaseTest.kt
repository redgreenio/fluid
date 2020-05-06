package io.redgreen.fluid.engine.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.assist.getTestArtifact
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.DoesNotImplementGeneratorInterface
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.JarNotFound
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.MissingDefaultConstructor
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.MissingGeneratorClassSpecifiedInManifest
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.MissingManifestAttributes
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.NotGeneratorJar
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.engine.model.GeneratorEntry
import io.redgreen.fluid.engine.model.MaintainerEntry
import io.redgreen.fluid.engine.model.Manifest
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class ValidateGeneratorUseCaseTest {
  private val useCase = ValidateGeneratorUseCase()

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
    val notJarPath = getTestArtifact("not-a-jar.png")

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
    assertThat(result)
      .isEqualTo(getExpectedValidGenerator(result.generatorClass.classLoader))
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

  private fun getExpectedValidGenerator(generatorClassLoader: ClassLoader): ValidGenerator {
    val generator = GeneratorEntry(
      "generator-id",
      "com.example.generator.LibraryProjectGenerator",
      "Name",
      "Description",
      "0.1.0"
    )
    val maintainer = MaintainerEntry("Acme Inc.,", "https://example.com", "oss@example.com")
    val generatorClass = generatorClassLoader
      .loadClass("com.example.generator.LibraryProjectGenerator")
      .asSubclass(Generator::class.java)
    return ValidGenerator(
      Manifest(generator, maintainer),
      generatorClass,
      "8386ed46db1f8a273a348d0863b905d4238e17d3a123b030125fd336706f457c",
      Paths.get("src/test/resources/jar-test-artifacts/valid-generator.jar")
    )
  }
}
