package io.redgreen.fluid.cli.internal

import picocli.CommandLine.Command

@Command(
  headerHeading = "Fluid (Pre-Alpha)\n",
  mixinStandardHelpOptions = true,
  helpCommand = true
)
class FluidCommand {
  companion object {
    internal const val EXIT_CODE_SUCCESS = 0
  }
}
