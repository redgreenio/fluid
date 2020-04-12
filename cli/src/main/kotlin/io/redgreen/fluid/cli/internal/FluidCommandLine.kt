package io.redgreen.fluid.cli.internal

import picocli.CommandLine.Command

@Command(
  headerHeading = "Fluid (Pre-Alpha)",
  subcommands = [RunCommand::class]
)
class FluidCommandLine
