package io.redgreen.fluid.cli

import io.redgreen.fluid.cli.internal.FluidCommandLine
import io.redgreen.fluid.cli.internal.InstallCommand
import io.redgreen.fluid.cli.internal.RunCommand
import picocli.CommandLine
import picocli.CommandLine.ParseResult
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  exitProcess(executeCommand(parseArgs(System.getProperty("user.home"), args)))
}

private fun parseArgs(
  userHome: String,
  args: Array<String>
): ParseResult {
  val userHomeDir = Paths.get(userHome)

  return CommandLine(FluidCommandLine())
    .addSubcommand(InstallCommand(userHomeDir))
    .addSubcommand(RunCommand(userHomeDir))
    .parseArgs(*args)
}

private fun executeCommand(parseResult: ParseResult): Int {
  return when (val subcommand = parseResult.subcommand().commandSpec().userObject()) {
    is InstallCommand -> subcommand.call()
    is RunCommand -> subcommand.call()
    else -> throw UnsupportedOperationException("Unknown command: ${subcommand::class.java.simpleName}")
  }
}
