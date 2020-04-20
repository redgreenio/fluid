package io.redgreen.fluid.registry.domain

import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Lookup.InstallLookup
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.AlreadyInstalled
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentHashes
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentVersions
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.NotInstalled
import io.redgreen.fluid.registry.model.Registry

class LookupGeneratorUseCase(
  private val registry: Registry
) {
  fun invoke(
    lookup: Lookup
  ): Result {
    val installLookup = lookup as InstallLookup
    val registryEntryOptional = registry.getRegistryEntry(installLookup.id)

    return if (registryEntryOptional.isPresent) {
      val registryEntry = registryEntryOptional.get()
      val artifactPath = registry.artifactsPath.resolve(registryEntry.artifactName)
      val installed = ValidateGeneratorUseCase().invoke(artifactPath) as ValidGenerator
      installLookupGeneratorInRegistry(installed, installLookup)
    } else {
      NotInstalled
    }
  }

  private fun installLookupGeneratorInRegistry(
    installed: ValidGenerator,
    installLookup: InstallLookup
  ): Result {
    val isFingerprintEqual = installed.sha256 == installLookup.sha256
    val isVersionEqual = installed.manifest.generator.version == installLookup.version

    return when {
      isFingerprintEqual -> AlreadyInstalled
      isVersionEqual -> DifferentHashes(installed.sha256, installLookup.sha256)
      else -> DifferentVersions(installed.manifest.generator.version, installLookup.version)
    }
  }

  sealed class Lookup {
    data class InstallLookup(
      val id: String,
      val sha256: String,
      val version: String
    ) : Lookup() {
      companion object {
        fun from(candidate: ValidGenerator): InstallLookup {
          val generatorEntry = candidate.manifest.generator
          return InstallLookup(generatorEntry.id, candidate.sha256, generatorEntry.version)
        }
      }
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
    ) : Result()
  }
}
