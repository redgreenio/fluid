package io.redgreen.fluid.cli

import io.redgreen.fluid.cli.internal.FluidCommand
import io.redgreen.fluid.cli.internal.FluidCommand.Companion.EXIT_CODE_SUCCESS
import io.redgreen.fluid.cli.internal.InstallCommand
import io.redgreen.fluid.cli.internal.RunCommand
import picocli.CommandLine
import picocli.CommandLine.Help.Ansi
import picocli.CommandLine.ParseResult
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  val userHome = System.getProperty("user.home")
  val userHomeDir = Paths.get(userHome)

  val fluidCommand = FluidCommand()
  val commandLine = CommandLine(fluidCommand)
    .addSubcommand(InstallCommand(userHomeDir))
    .addSubcommand(RunCommand(userHomeDir))

  val parseResult = commandLine
    .parseArgs(*args)

  exitProcess(handleSubcommand(commandLine, parseResult))
}

private fun handleSubcommand(
  commandLine: CommandLine,
  parseResult: ParseResult
): Int {
  return when (val subcommand = parseResult.subcommand()?.commandSpec()?.userObject()) {
    is InstallCommand -> subcommand.call()
    is RunCommand -> subcommand.call()
    else -> printHelp(commandLine)
  }
}

private fun printHelp(commandLine: CommandLine): Int {
  commandLine.usage(System.out, Ansi.ON)
  return EXIT_CODE_SUCCESS
}
