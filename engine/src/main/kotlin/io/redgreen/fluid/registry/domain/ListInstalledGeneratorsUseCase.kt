package io.redgreen.fluid.registry.domain

import io.redgreen.fluid.registry.Registry
import io.redgreen.fluid.registry.domain.ListInstalledGeneratorsUseCase.Result.InstalledGenerators
import io.redgreen.fluid.registry.domain.ListInstalledGeneratorsUseCase.Result.NoGeneratorsInstalled
import io.redgreen.fluid.registry.model.InstalledGenerator

class ListInstalledGeneratorsUseCase(
  private val registry: Registry
) {
  fun invoke(): Result {
    val entries = registry.getEntries()
    return if (entries.isEmpty()) {
      NoGeneratorsInstalled
    } else {
      val generators = entries.map { InstalledGenerator(it.id) }
      InstalledGenerators(generators)
    }
  }

  sealed class Result {
    object NoGeneratorsInstalled : Result()

    data class InstalledGenerators(
      val generators: List<InstalledGenerator>
    ) : Result()
  }
}
