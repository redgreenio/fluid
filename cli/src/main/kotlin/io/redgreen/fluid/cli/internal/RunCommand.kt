package io.redgreen.fluid.cli.internal

import io.redgreen.fluid.cli.ui.Printer
import io.redgreen.fluid.engine.domain.RunGeneratorUseCase
import io.redgreen.fluid.engine.domain.RunGeneratorUseCase.Result.GeneratorNotFound
import io.redgreen.fluid.engine.domain.RunGeneratorUseCase.Result.RunSuccessful
import io.redgreen.fluid.registry.model.Registry
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.nio.file.Path
import java.util.concurrent.Callable

@Command(name = "run")
class RunCommand(
  private val userHomeDir: Path
) : Callable<Int> {
  @Parameters(index = "0")
  internal lateinit var generatorId: String

  @Parameters(index = "1")
  internal lateinit var destination: Path

  private val registry by lazy { Registry.from(userHomeDir) }

  override fun call(): Int {
    when (val result = RunGeneratorUseCase(registry).invoke(generatorId, destination)) {
      is RunSuccessful -> result.realizations.onEach { Printer.println { it.path } }
      GeneratorNotFound -> Printer.println { "$generatorId is not installed." }
    }
    return 0
  }
}
