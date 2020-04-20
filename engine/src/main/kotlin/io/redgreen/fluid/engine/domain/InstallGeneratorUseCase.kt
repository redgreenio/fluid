package io.redgreen.fluid.engine.domain

import com.squareup.moshi.Moshi
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.InstallationType.FRESH
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.InstallationType.OVERWRITE
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.Result.FreshInstallSuccessful
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.Result.OverwriteSuccessful
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.domain.AddRegistryEntryUseCase
import io.redgreen.fluid.registry.domain.CopyGeneratorUseCase
import io.redgreen.fluid.registry.domain.CopyGeneratorUseCase.Result.GeneratorCopied
import io.redgreen.fluid.registry.domain.UpdateRegistryUseCase
import io.redgreen.fluid.registry.model.Registry
import io.redgreen.fluid.registry.model.RegistryEntry
import java.nio.file.Files
import java.nio.file.Path

class InstallGeneratorUseCase(
  private val registry: Registry,
  private val moshi: Moshi
) {
  fun invoke(
    candidate: ValidGenerator,
    installationType: InstallationType
  ): Result {
    if (installationType == OVERWRITE) {
      deleteAlreadyInstalledArtifact(candidate.manifest.generator.id)
    }

    return when (val result = CopyGeneratorUseCase(registry).invoke(candidate)) {
      is GeneratorCopied -> {
        val artifactName = getArtifactName(result.destinationPath)
        val generatorId = result.manifest.generator.id
        val entry = RegistryEntry(generatorId, artifactName)

        when (installationType) {
          FRESH -> {
            AddRegistryEntryUseCase(registry, moshi).invoke(entry)
            FreshInstallSuccessful(entry)
          }

          OVERWRITE -> {
            UpdateRegistryUseCase(registry, moshi).invoke(entry)
            OverwriteSuccessful(entry)
          }
        }
      }
    }
  }

  private fun getArtifactName(artifactPath: Path): String =
    artifactPath.fileName.toString()

  private fun deleteAlreadyInstalledArtifact(generatorId: String) {
    val existingEntryForGenerator = registry.getRegistryEntry(generatorId)
    if (existingEntryForGenerator.isPresent) {
      val alreadyInstalledArtifactPath = registry.artifactsPath.resolve(existingEntryForGenerator.get().artifactName)
      Files.delete(alreadyInstalledArtifactPath)
    }
  }

  sealed class Result {
    data class FreshInstallSuccessful(
      val registryEntry: RegistryEntry
    ) : Result()

    data class OverwriteSuccessful(
      val registryEntry: RegistryEntry
    ) : Result()
  }

  enum class InstallationType {
    FRESH, OVERWRITE
  }
}
