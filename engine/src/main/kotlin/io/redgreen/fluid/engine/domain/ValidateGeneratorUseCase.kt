package io.redgreen.fluid.engine.domain

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.DoesNotImplementGeneratorInterface
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.JarNotFound
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.MissingDefaultConstructor
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.MissingGeneratorClassSpecifiedInManifest
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.MissingManifestAttributes
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.NotGeneratorJar
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.engine.domain.ValidateManifestJsonUseCase.Result.Valid
import io.redgreen.fluid.engine.model.Manifest
import io.redgreen.fluid.extensions.computeSha256
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional
import java.util.zip.ZipException
import java.util.zip.ZipFile

class ValidateGeneratorUseCase {
  companion object {
    private const val MANIFEST_FILE_NAME = "META-INF/manifest.json"
  }

  fun invoke(artifactPath: Path): Result {
    if (!Files.exists(artifactPath)) {
      return JarNotFound(artifactPath)
    }

    val zipFile = try {
      ZipFile(artifactPath.toFile())
    } catch (e: ZipException) {
      e.printStackTrace()
      return NotGeneratorJar(artifactPath)
    }

    val manifestValidationResultOptional = validateManifestJson(zipFile)
    return if (!manifestValidationResultOptional.isPresent) {
      NotGeneratorJar(artifactPath)
    } else {
      val validationResult = manifestValidationResultOptional.get()
      if (validationResult is Valid) {
        validateGeneratorClassSpecifiedInManifest(artifactPath, validationResult.manifest)
      } else {
        MissingManifestAttributes(artifactPath)
      }
    }
  }

  private fun validateManifestJson(zipFile: ZipFile): Optional<ValidateManifestJsonUseCase.Result> {
    val entries = zipFile.entries()

    for (entry in entries) {
      if (entry.name != MANIFEST_FILE_NAME) continue
      val manifestJson = zipFile.getInputStream(entry).reader().readText()
      val useCase = ValidateManifestJsonUseCase()
      return Optional.of(useCase.invoke(manifestJson))
    }

    return Optional.empty()
  }

  private fun validateGeneratorClassSpecifiedInManifest(
    artifactPath: Path,
    manifest: Manifest
  ): Result {
    val generatorClassName = manifest.generator.implementation
    return try {
      val loadedClass = getGeneratorClassLoader(artifactPath).loadClass(generatorClassName)
      if (!Generator::class.java.isAssignableFrom(loadedClass)) {
        DoesNotImplementGeneratorInterface(artifactPath, loadedClass.name)
      } else {
        val generatorClass = loadedClass.asSubclass(Generator::class.java)
        validateClassImplementingGeneratorType(artifactPath, generatorClass, manifest)
      }
    } catch (e: ClassNotFoundException) {
      MissingGeneratorClassSpecifiedInManifest(artifactPath, generatorClassName)
    }
  }

  private fun getGeneratorClassLoader(artifactPath: Path): ClassLoader =
    URLClassLoader(arrayOf(artifactPath.toUri().toURL()))

  private fun validateClassImplementingGeneratorType(
    artifactPath: Path,
    loadedClass: Class<out Generator>,
    manifest: Manifest
  ): Result {
    return try {
      loadedClass.getConstructor()
      val sha256 = computeSha256(Files.newInputStream(artifactPath))
      ValidGenerator(manifest, loadedClass, sha256, artifactPath)
    } catch (e: NoSuchMethodException) {
      MissingDefaultConstructor(artifactPath, manifest.generator.implementation)
    }
  }

  sealed class Result(open val artifactPath: Path) {
    /**
     * The specified path does not contain a file.
     */
    data class JarNotFound(override val artifactPath: Path) : Result(artifactPath)

    /**
     * The specified path has a file, but it is not a generator jar file.
     */
    data class NotGeneratorJar(override val artifactPath: Path) : Result(artifactPath)

    /**
     * The jar file does not contain the mandatory 'Generator' attribute.
     */
    data class MissingManifestAttributes(override val artifactPath: Path) : Result(artifactPath)

    /**
     * The jar file contains a 'Generator' attribute, but the class specified by the attribute is missing
     * from the jar.
     */
    data class MissingGeneratorClassSpecifiedInManifest(
      override val artifactPath: Path,
      val missingClassName: String
    ) : Result(artifactPath)

    /**
     * The class mentioned by the manifest's 'Generator' attribute does not implement the @see[Generator]
     * interface.
     */
    data class DoesNotImplementGeneratorInterface(
      override val artifactPath: Path,
      val foundClassName: String
    ) : Result(artifactPath)

    /**
     * The generator class specified in the manifest does not have a default constructor.
     */
    data class MissingDefaultConstructor(
      override val artifactPath: Path,
      val generatorClassName: String
    ) : Result(artifactPath)

    /**
     * The generator contains a valid @see[Generator] implementation.
     */
    data class ValidGenerator internal constructor(
      val manifest: Manifest,
      val generatorClass: Class<out Generator>,
      val sha256: String,
      override val artifactPath: Path
    ) : Result(artifactPath)
  }
}
