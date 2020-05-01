package io.redgreen.fluid.registry.domain

import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.Registry
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Lookup.InstallLookup
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Lookup.RunLookup
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.AlreadyInstalled
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentHashes
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentVersions
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.NotInstalled
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.ReadyToRun

class LookupGeneratorUseCase(
  private val registry: Registry
) {
  fun invoke(
    lookup: Lookup
  ): Result {
    val registryEntryOptional = registry.getEntryById(lookup.id)

    return if (registryEntryOptional.isPresent) {
      val registryEntry = registryEntryOptional.get()
      val artifactPath = registry.artifactsPath.resolve(registryEntry.artifactName)
      val installed = ValidateGeneratorUseCase().invoke(artifactPath) as ValidGenerator
      when (lookup) {
        is InstallLookup -> lookupGeneratorInRegistryForInstallation(installed, lookup)
        is RunLookup -> ReadyToRun(installed)
      }
    } else {
      NotInstalled
    }
  }

  private fun lookupGeneratorInRegistryForInstallation(
    installed: ValidGenerator,
    lookup: InstallLookup
  ): Result {
    val isFingerprintEqual = installed.sha256 == lookup.sha256
    val isVersionEqual = installed.manifest.generator.version == lookup.version

    return when {
      isFingerprintEqual -> AlreadyInstalled
      isVersionEqual -> DifferentHashes(installed.sha256, lookup.sha256)
      else -> DifferentVersions(installed.manifest.generator.version, lookup.version)
    }
  }

  sealed class Lookup(
    open val id: String
  ) {
    data class InstallLookup(
      override val id: String,
      val sha256: String,
      val version: String
    ) : Lookup(id) {
      companion object {
        fun from(candidate: ValidGenerator): InstallLookup {
          val generatorEntry = candidate.manifest.generator
          return InstallLookup(generatorEntry.id, candidate.sha256, generatorEntry.version)
        }
      }
    }

    data class RunLookup(
      override val id: String
    ) : Lookup(id)
  }

  sealed class Result {
    object NotInstalled : Result() // TODO Should objects contain more information?

    object AlreadyInstalled : Result() // TODO Change terminology to `Installed`?

    data class DifferentHashes(
      val installed: String,
      val candidate: String
    ) : Result()

    data class DifferentVersions(
      val installed: String,
      val candidate: String
    ) : Result()

    data class ReadyToRun(
      val installed: ValidGenerator
    ) : Result() // TODO To be paired with `RunLookup` with two states. Possibly nested sealed classes?
  }
}
