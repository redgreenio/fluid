package io.redgreen.fluid.cli.internal

import com.github.tomaslanger.chalk.Chalk
import io.redgreen.fluid.cli.internal.FluidCommand.EXIT_CODE_SUCCESS
import io.redgreen.fluid.cli.ui.Printer
import io.redgreen.fluid.registry.Registry
import io.redgreen.fluid.registry.domain.ListInstalledGeneratorsUseCase
import io.redgreen.fluid.registry.domain.ListInstalledGeneratorsUseCase.Result.InstalledGenerators
import io.redgreen.fluid.registry.domain.ListInstalledGeneratorsUseCase.Result.NoGeneratorsInstalled
import io.redgreen.fluid.registry.model.InstalledGenerator
import picocli.CommandLine.Command
import java.util.concurrent.Callable

@Command(
  name = "generators",
  description = ["View information about installed generators"]
)
class GeneratorsCommand(
  private val registry: Registry
) : Callable<Int> {
  override fun call(): Int {
    when (val result = ListInstalledGeneratorsUseCase(registry).invoke()) {
      is NoGeneratorsInstalled -> printNoGeneratorsInstalledMessage()
      is InstalledGenerators -> printInstalledGenerators(result.generators)
    }
    return EXIT_CODE_SUCCESS
  }

  private fun printNoGeneratorsInstalledMessage() {
    Printer.println {
      val installCommand = Chalk.on("fluid install").bold()
      "No generators installed. Use `$installCommand` to install one (or a lot)!"
    }
  }

  private fun printInstalledGenerators(
    generators: List<InstalledGenerator>
  ) {
    Printer.println { "Installed generators:" }
    generators.forEach { Printer.println { it.id } }
  }
}
