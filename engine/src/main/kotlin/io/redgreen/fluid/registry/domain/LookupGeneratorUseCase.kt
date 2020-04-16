package io.redgreen.fluid.registry.domain

import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.AlreadyInstalled
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentHashes
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentVersions
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.NotInstalled
import io.redgreen.fluid.registry.model.Registry
import io.redgreen.fluid.registry.model.VersionComparison

class LookupGeneratorUseCase {
  fun invoke(
    registry: Registry,
    generatorToInstall: ValidGenerator
  ): Result {
    val registryEntryOptional = registry
      .getRegistryEntry(generatorToInstall.manifest.generator.id)

    return if (registryEntryOptional.isPresent) {
      val registryEntry = registryEntryOptional.get()
      val artifactPath = registry.artifactsPath.resolve(registryEntry.artifactName)
      val installedGenerator = ValidateGeneratorUseCase().invoke(artifactPath) as ValidGenerator
      lookupGeneratorInRegistry(installedGenerator, generatorToInstall)
    } else {
      NotInstalled
    }
  }

  private fun lookupGeneratorInRegistry(
    installed: ValidGenerator,
    candidate: ValidGenerator
  ): Result {
    val isFingerprintEqual = installed.sha256 == candidate.sha256
    val isVersionEqual = installed.manifest.generator.version == candidate.manifest.generator.version

    return when {
      isFingerprintEqual -> AlreadyInstalled
      isVersionEqual -> DifferentHashes(installed.sha256, candidate.sha256)
      else -> DifferentVersions(installed.manifest.generator.version, candidate.manifest.generator.version)
    }
  }

  sealed class Result {
    object NotInstalled : Result()
    object AlreadyInstalled : Result()

    data class DifferentHashes(
      val installed: String,
      val candidate: String
    ) : Result()

    data class DifferentVersions(
      val installed: String,
      val candidate: String
    ) : Result() {
      fun compare(): VersionComparison =
        VersionComparison.compare(installed, candidate)
    }
  }
}
