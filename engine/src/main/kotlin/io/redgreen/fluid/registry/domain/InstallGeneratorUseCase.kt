package io.redgreen.fluid.registry.domain

import com.squareup.moshi.Moshi
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.domain.CopyGeneratorUseCase.Result.GeneratorCopied
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase.Result.GeneratorInstalled
import io.redgreen.fluid.registry.model.RegistryEntry
import io.redgreen.fluid.registry.model.RegistryHome
import java.nio.file.Path

class InstallGeneratorUseCase(
  private val registryHome: RegistryHome,
  private val moshi: Moshi
) {
  fun invoke(validGenerator: ValidGenerator): Result {
    return when (val result = CopyGeneratorUseCase(registryHome).invoke(validGenerator)) {
      is GeneratorCopied -> {
        val entry = RegistryEntry(getRelativePath(result.destinationPath).toString())
        AddRegistryEntryUseCase(registryHome, moshi).invoke(entry)
        GeneratorInstalled(entry)
      }
    }
  }

  private fun getRelativePath(jarPath: Path): Path {
    val requiredNames = 2 // the "libs" directory and the "jar" path names
    return jarPath.subpath(jarPath.nameCount - requiredNames, jarPath.nameCount)
  }

  sealed class Result {
    data class GeneratorInstalled(
      val registryEntry: RegistryEntry
    ) : Result()
  }
}
