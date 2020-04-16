package io.redgreen.fluid.registry.domain

import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.domain.GeneratorLookupUseCase.Result.AlreadyInstalled
import io.redgreen.fluid.registry.domain.GeneratorLookupUseCase.Result.HashesDiffer
import io.redgreen.fluid.registry.domain.GeneratorLookupUseCase.Result.NotInstalled
import io.redgreen.fluid.registry.domain.GeneratorLookupUseCase.Result.VersionsDiffer
import io.redgreen.fluid.registry.model.Registry

class GeneratorLookupUseCase {
  fun invoke(
    registry: Registry,
    generatorToInstall: ValidGenerator
  ): Result {
    val registryEntryOptional = registry
      .getRegistryEntry(generatorToInstall.manifest.generator.id)

    return if (registryEntryOptional.isPresent) {
      val registryEntry = registryEntryOptional.get()
      val artifactPath = registry.path.resolve(registryEntry.relativeArtifactPath)
      val installedGenerator = ValidateGeneratorJarUseCase().invoke(artifactPath) as ValidGenerator

      if (installedGenerator.sha256 == generatorToInstall.sha256) {
        AlreadyInstalled
      } else if (installedGenerator.manifest.generator.version == generatorToInstall.manifest.generator.version) {
        HashesDiffer(installedGenerator.sha256, generatorToInstall.sha256)
      } else {
        VersionsDiffer(installedGenerator.manifest.generator.version, generatorToInstall.manifest.generator.version)
      }
    } else {
      NotInstalled
    }
  }

  sealed class Result {
    object NotInstalled : Result()
    object AlreadyInstalled : Result()

    data class HashesDiffer(
      val installedGeneratorHash: String,
      val generatorToInstallHash: String
    ) : Result()

    data class VersionsDiffer(
      val installedGeneratorVersion: String,
      val generatorToInstallVersion: String
    ) : Result()
  }
}
