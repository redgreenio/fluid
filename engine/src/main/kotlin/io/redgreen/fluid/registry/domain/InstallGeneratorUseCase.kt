package io.redgreen.fluid.registry.domain

import com.squareup.moshi.Moshi
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.domain.CopyGeneratorUseCase.Result.GeneratorCopied
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase.Result.FreshInstallSuccessful
import io.redgreen.fluid.registry.model.Registry
import io.redgreen.fluid.registry.model.RegistryEntry
import java.nio.file.Path

class InstallGeneratorUseCase(
  private val registry: Registry,
  private val moshi: Moshi
) {
  fun invoke(
    candidate: ValidGenerator,
    installationType: InstallationType
  ): Result {
    return when (val result = CopyGeneratorUseCase(registry).invoke(candidate)) {
      is GeneratorCopied -> {
        val artifactName = getArtifactName(result.destinationPath)
        val generatorId = result.manifest.generator.id
        val entry = RegistryEntry(generatorId, artifactName)
        AddRegistryEntryUseCase(registry, moshi).invoke(entry)
        FreshInstallSuccessful(entry)
      }
    }
  }

  private fun getArtifactName(artifactPath: Path): String =
    artifactPath.fileName.toString()

  sealed class Result {
    data class FreshInstallSuccessful(
      val registryEntry: RegistryEntry
    ) : Result()
  }

  enum class InstallationType {
    FRESH
  }
}
