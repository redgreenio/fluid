package io.redgreen.fluid.engine.domain

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.engine.Realizer
import io.redgreen.fluid.engine.domain.RunGeneratorUseCase.Result.GeneratorNotFound
import io.redgreen.fluid.engine.domain.RunGeneratorUseCase.Result.RunSuccessful
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.engine.model.Realization
import io.redgreen.fluid.registry.Registry
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Lookup.RunLookup
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.NotInstalled
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.ReadyToRun
import io.redgreen.fluid.snapshot.InMemorySnapshotFactory
import java.nio.file.Path

class RunGeneratorUseCase(
  private val registry: Registry
) {
  fun invoke(
    generatorId: String,
    destinationPath: Path
  ): Result {
    return when (val result = LookupGeneratorUseCase(registry).invoke(RunLookup(generatorId))) {
      NotInstalled -> GeneratorNotFound
      is ReadyToRun -> runGenerator(result, destinationPath)
      else -> throw IllegalStateException("This cannot happen, illegal result: $result")
    }
  }

  private fun runGenerator(
    result: ReadyToRun,
    destination: Path
  ): RunSuccessful {
    // FIXME This is too cumbersome to work with. We should make this more intuitive to use by providing more information.
    val validGenerator = ValidateGeneratorUseCase().invoke(result.installed.artifactPath) as ValidGenerator
    val generatorClass = validGenerator.generatorClass
    val generator = generatorClass.getDeclaredConstructor().newInstance() as Generator<*>
    val snapshot = generator.scaffold().buildSnapshot(InMemorySnapshotFactory(), generatorClass)
    val realizations = Realizer().realize(destination.toFile(), snapshot)
    return RunSuccessful(realizations)
  }

  sealed class Result {
    data class RunSuccessful(
      val realizations: List<Realization>
    ) : Result()

    object GeneratorNotFound : Result()
  }
}
