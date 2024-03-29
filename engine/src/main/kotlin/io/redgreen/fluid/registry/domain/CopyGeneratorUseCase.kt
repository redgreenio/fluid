package io.redgreen.fluid.registry.domain

import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.engine.model.Manifest
import io.redgreen.fluid.registry.Registry
import io.redgreen.fluid.registry.domain.CopyGeneratorUseCase.Result.GeneratorCopied
import java.nio.file.Files
import java.nio.file.Path

class CopyGeneratorUseCase(
  private val registry: Registry
) {
  private val registryGeneratorsDirPath by lazy {
    registry.artifactsPath
  }

  fun invoke(validGenerator: ValidGenerator): Result {
    val sourcePath = validGenerator.artifactPath
    val artifactFileName = sourcePath.fileName.toString()
    val destinationPath = registryGeneratorsDirPath.resolve(artifactFileName)

    if (!Files.exists(registryGeneratorsDirPath)) {
      Files.createDirectories(registryGeneratorsDirPath)
    }

    Files.copy(sourcePath, destinationPath)
    return GeneratorCopied(destinationPath, validGenerator.manifest)
  }

  sealed class Result {
    data class GeneratorCopied(
      val destinationPath: Path,
      val manifest: Manifest
    ) : Result()
  }
}
