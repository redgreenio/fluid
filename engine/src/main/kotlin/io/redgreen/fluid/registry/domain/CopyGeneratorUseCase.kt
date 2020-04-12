package io.redgreen.fluid.registry.domain

import io.redgreen.fluid.engine.model.GeneratorJar
import io.redgreen.fluid.registry.domain.CopyGeneratorUseCase.Result.GeneratorCopied
import io.redgreen.fluid.registry.model.RegistryHome
import java.nio.file.Files
import java.nio.file.Path

class CopyGeneratorUseCase(
  private val registryHome: RegistryHome
) {
  companion object {
    private const val GENERATORS_DIR = "libs"
  }

  private val registryGeneratorsDirPath by lazy {
    registryHome.path.resolve(GENERATORS_DIR).toAbsolutePath()
  }

  fun invoke(generatorJar: GeneratorJar): Result {
    val sourceJarPath = generatorJar.path
    val generatorJarFileName = sourceJarPath.fileName.toString()
    val destinationJarPath = registryGeneratorsDirPath.resolve(generatorJarFileName)

    if (!Files.exists(registryGeneratorsDirPath)) {
      Files.createDirectories(registryGeneratorsDirPath)
    }

    Files.copy(sourceJarPath, destinationJarPath)
    return GeneratorCopied(destinationJarPath)
  }

  sealed class Result {
    data class GeneratorCopied(
      val destinationPath: Path
    ) : Result()
  }
}