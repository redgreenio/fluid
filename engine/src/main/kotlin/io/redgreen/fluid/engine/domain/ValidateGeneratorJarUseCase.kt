package io.redgreen.fluid.engine.domain

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.DoesNotImplementGeneratorInterface
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.JarNotFound
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.ManifestMissingGeneratorAttribute
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.MissingDefaultConstructor
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.MissingGeneratorClassSpecifiedInManifest
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.NotGeneratorJar
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.ValidGenerator
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarInputStream
import java.util.jar.Manifest

class ValidateGeneratorJarUseCase {
  companion object {
    private const val KEY_GENERATOR = "Generator"
  }

  fun invoke(artifactPath: Path): Result {
    if (!Files.exists(artifactPath)) {
      return JarNotFound(artifactPath)
    }

    val jarManifest = getJarManifest(artifactPath) // TODO This information will be derived from an XML document
      ?: return NotGeneratorJar(artifactPath)

    val generatorClassName = getGeneratorClassName(jarManifest) // TODO Possibly replace with a ValidatePluginManifestUseCase??
      ?: return ManifestMissingGeneratorAttribute(artifactPath)

    return validateGeneratorClassSpecifiedInManifest(
      artifactPath,
      generatorClassName
    )
  }

  private fun getJarManifest(maybeArtifactPath: Path): Manifest? =
    Files.newInputStream(maybeArtifactPath).use { inputStream ->
      JarInputStream(inputStream).use { jarStream ->
        return jarStream.manifest
      }
    }

  private fun getGeneratorClassName(manifest: Manifest): String? =
    manifest.mainAttributes.getValue(KEY_GENERATOR)

  private fun validateGeneratorClassSpecifiedInManifest(
    artifactPath: Path,
    generatorClassName: String
  ): Result {
    return try {
      val loadedClass = getGeneratorClassLoader(artifactPath).loadClass(generatorClassName)
      if (!Generator::class.java.isAssignableFrom(loadedClass)) {
        DoesNotImplementGeneratorInterface(artifactPath, loadedClass.name)
      } else {
        val generatorClass = loadedClass.asSubclass(Generator::class.java)
        validateClassImplementingGeneratorType(artifactPath, generatorClass, generatorClassName)
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
    generatorClassName: String
  ): Result {
    return try {
      loadedClass.getConstructor()
      ValidGenerator(artifactPath, loadedClass)
    } catch (e: NoSuchMethodException) {
      MissingDefaultConstructor(artifactPath, generatorClassName)
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
    data class ManifestMissingGeneratorAttribute(override val artifactPath: Path) : Result(artifactPath)

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
      override val artifactPath: Path,
      val generatorClass: Class<out Generator>
    ) : Result(artifactPath)
  }
}
