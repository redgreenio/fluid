package io.redgreen.fluid.registry.domain

import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.ValidGenerator
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

  fun invoke(validGenerator: ValidGenerator): Result {
    val sourcePath = validGenerator.artifactPath
    val artifactFileName = sourcePath.fileName.toString()
    val destinationPath = registryGeneratorsDirPath.resolve(artifactFileName)

    if (!Files.exists(registryGeneratorsDirPath)) {
      Files.createDirectories(registryGeneratorsDirPath)
    }

    Files.copy(sourcePath, destinationPath)
    return GeneratorCopied(destinationPath)
  }

  sealed class Result {
    data class GeneratorCopied(
      val destinationPath: Path
    ) : Result()
  }
}
