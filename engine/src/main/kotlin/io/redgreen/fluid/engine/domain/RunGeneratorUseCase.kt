package io.redgreen.fluid.engine.domain

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.engine.Realizer
import io.redgreen.fluid.engine.model.Realization
import io.redgreen.fluid.snapshot.InMemorySnapshotFactory
import java.io.File

class RunGeneratorUseCase {
  fun invoke(
    generatorClass: Class<out Generator>,
    destinationDir: File
  ) : Result {
    val generator = generatorClass.getDeclaredConstructor().newInstance() as Generator
    val snapshot = generator.scaffold().buildSnapshot(InMemorySnapshotFactory(), generatorClass)
    return Result(Realizer().realize(destinationDir, snapshot))
  }

  data class Result(
    val realizations: List<Realization>
  )
}
