package io.redgreen.fluid.cli

import io.redgreen.fluid.cli.internal.FluidCommandLine
import io.redgreen.fluid.cli.internal.RunCommand
import picocli.CommandLine
import kotlin.system.exitProcess

fun main() {
  val args = arrayOf("run", "generator", "hello-world")
  val parseResult = CommandLine(FluidCommandLine())
    .parseArgs(*args)

  val result = when (val subcommand = parseResult.subcommand().commandSpec().userObject()) {
    is RunCommand -> subcommand.call()
    else -> throw IllegalStateException("Unknown subcommand: ${subcommand::class.java.name}")
  }

  exitProcess(result)
}
