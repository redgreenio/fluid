package io.redgreen.fluid.engine.domain

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.DoesNotImplementGeneratorInterface
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.JarNotFound
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.ManifestMissingGeneratorAttribute
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.MissingDefaultConstructor
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.MissingGeneratorClassSpecifiedInManifest
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.NotGeneratorJar
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.ValidGenerator
import io.redgreen.fluid.engine.model.GeneratorJar
import java.io.File
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.jar.JarInputStream
import java.util.jar.Manifest

class LoadGeneratorJarUseCase {
  companion object {
    private const val KEY_GENERATOR = "Generator"
  }

  fun invoke(jarPath: String): Result { // TODO Convert this to path?
    val maybeJarFile = File(jarPath)

    if (!maybeJarFile.exists()) return JarNotFound(jarPath)

    val jarManifest = getJarManifest(maybeJarFile)
      ?: return NotGeneratorJar(jarPath)

    val generatorClassName = getGeneratorClassName(jarManifest)
      ?: return ManifestMissingGeneratorAttribute(jarPath)

    return validateGeneratorClassSpecifiedInManifest(jarPath, generatorClassName)
  }

  private fun getJarManifest(maybeJarFile: File): Manifest? =
    JarInputStream(maybeJarFile.inputStream()).manifest

  private fun getGeneratorClassName(manifest: Manifest): String? =
    manifest.mainAttributes.getValue(KEY_GENERATOR)

  private fun getGeneratorClassLoader(jarPath: String): ClassLoader =
    URLClassLoader(arrayOf(File(jarPath).toURI().toURL()))

  private fun validateGeneratorClassSpecifiedInManifest(
    jarPath: String,
    generatorClassName: String
  ): Result {
    return try {
      val loadedClass = getGeneratorClassLoader(jarPath).loadClass(generatorClassName)
      if (!Generator::class.java.isAssignableFrom(loadedClass)) {
        DoesNotImplementGeneratorInterface(jarPath, loadedClass.name)
      } else {
        validateClassImplementingGeneratorType(jarPath, loadedClass.asSubclass(Generator::class.java), generatorClassName)
      }
    } catch (e: ClassNotFoundException) {
      MissingGeneratorClassSpecifiedInManifest(jarPath, generatorClassName)
    }
  }

  private fun validateClassImplementingGeneratorType(
    jarPath: String,
    loadedClass: Class<out Generator>,
    generatorClassName: String
  ): Result {
    return try {
      loadedClass.getConstructor()
      ValidGenerator(jarPath, loadedClass)
    } catch (e: NoSuchMethodException) {
      MissingDefaultConstructor(jarPath, generatorClassName)
    }
  }

  sealed class Result(open val jarPath: String) {
    /**
     * The specified path does not contain a file.
     */
    data class JarNotFound(override val jarPath: String) : Result(jarPath)

    /**
     * The specified path has a file, but it is not a generator jar file.
     */
    data class NotGeneratorJar(override val jarPath: String) : Result(jarPath)

    /**
     * The jar file does not contain the mandatory 'Generator' attribute.
     */
    data class ManifestMissingGeneratorAttribute(override val jarPath: String) : Result(jarPath)

    /**
     * The jar file contains a 'Generator' attribute, but the class specified by the attribute is missing
     * from the jar.
     */
    data class MissingGeneratorClassSpecifiedInManifest(
      override val jarPath: String,
      val missingClassName: String
    ) : Result(jarPath)

    /**
     * The class mentioned by the manifest's 'Generator' attribute does not implement the @see[Generator]
     * interface.
     */
    data class DoesNotImplementGeneratorInterface(
      override val jarPath: String,
      val foundClassName: String
    ) : Result(jarPath)

    /**
     * The generator class specified in the manifest does not have a default constructor.
     */
    data class MissingDefaultConstructor(
      override val jarPath: String,
      val generatorClassName: String
    ) : Result(jarPath)

    /**
     * The generator contains a valid @see[Generator] implementation.
     */
    data class ValidGenerator(
      override val jarPath: String,
      val generatorClass: Class<out Generator> // TODO Populate this class with manifest jar attributes?
    ) : Result(jarPath) {
      fun generatorJar(): GeneratorJar =
        GeneratorJar(Path.of(jarPath), generatorClass)
    }
  }
}
