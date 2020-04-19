package io.redgreen.fluid.cli.internal

import com.github.tomaslanger.chalk.Chalk
import com.squareup.moshi.Moshi
import io.redgreen.fluid.cli.internal.FluidCommandLine.Companion.EXIT_CODE_SUCCESS
import io.redgreen.fluid.cli.internal.view.userMessage
import io.redgreen.fluid.cli.ui.Printer
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase.Result.FreshInstallSuccessful
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.AlreadyInstalled
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentHashes
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentVersions
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.NotInstalled
import io.redgreen.fluid.registry.model.Registry
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import java.util.Scanner
import java.util.concurrent.Callable

@Command(name = "install")
internal class InstallCommand(
  private val userHomeDir: Path
) : Callable<Int> {
  @Option(names = ["-j", "--jar"])
  internal lateinit var candidatePath: Path

  private val registry by lazy { Registry.from(userHomeDir) }
  private val validateGeneratorUseCase by lazy { ValidateGeneratorUseCase() }
  private val installGeneratorUseCase by lazy { InstallGeneratorUseCase(registry, Moshi.Builder().build()) }
  private val lookupGeneratorUseCase by lazy { LookupGeneratorUseCase() }

  override fun call(): Int {
    val validateCandidateResult = validateGeneratorUseCase.invoke(candidatePath)
    return if (validateCandidateResult is ValidGenerator) {
      val candidateGeneratorEntry = validateCandidateResult.manifest.generator

      when (val lookupResult = lookupGeneratorUseCase.invoke(registry, validateCandidateResult)) {
        NotInstalled -> performFreshInstall(validateCandidateResult)

        AlreadyInstalled -> printAlreadyInstalledMessage(lookupResult as AlreadyInstalled, candidateGeneratorEntry.id)

        is DifferentHashes -> {
          printDifferentHashesMessage(lookupResult, candidateGeneratorEntry.id, candidateGeneratorEntry.version)
          confirmWithUser({}, { Printer.println { "Installation aborted." } })
        }

        is DifferentVersions -> {
          printDifferentVersionsMessage(lookupResult, candidateGeneratorEntry.id)
          confirmWithUser({}, { Printer.println { "Installation aborted." } })
        }
      }

      EXIT_CODE_SUCCESS
    } else {
      TODO("$validateCandidateResult")
    }
  }

  private fun performFreshInstall(candidate: ValidGenerator) {
    val freshInstallSuccessful = installGeneratorUseCase.invoke(candidate) as FreshInstallSuccessful
    printFreshInstallSuccessfulMessage(freshInstallSuccessful, candidate.sha256)
  }

  private fun printFreshInstallSuccessfulMessage(
    freshInstallSuccessful: FreshInstallSuccessful,
    hash: String
  ) {
    Printer.println { freshInstallSuccessful.userMessage(hash) }
  }

  private fun printAlreadyInstalledMessage(
    alreadyInstalled: AlreadyInstalled,
    generatorId: String
  ) {
    Printer.println { alreadyInstalled.userMessage(generatorId) }
  }

  private fun printDifferentHashesMessage(
    differentHashes: DifferentHashes,
    generatorId: String,
    version: String
  ) {
    Printer.println { differentHashes.userMessage(generatorId, version) }
  }

  private fun printDifferentVersionsMessage(
    differentVersions: DifferentVersions,
    generatorId: String
  ) {
    Printer.println { differentVersions.userMessage(generatorId) }
  }

  private fun confirmWithUser(proceed: () -> Unit, abort: () -> Unit) {
    val meansYes = listOf("", "y", "yes", "ya", "yeah")
    val questionMark = Chalk.on("?").green().bold()
    val defaultYes = Chalk.on("Y").bold()
    print("$questionMark Do you want to proceed? ($defaultYes/n) ")
    val scanner = Scanner(BufferedReader(InputStreamReader(System.`in`)))
    val answer = scanner.nextLine().trim().toLowerCase()
    if (meansYes.contains(answer)) {
      proceed()
    } else {
      abort()
    }
  }
}
