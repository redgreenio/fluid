package io.redgreen.fluid.cli.internal

import com.github.tomaslanger.chalk.Chalk
import com.squareup.moshi.Moshi
import io.redgreen.fluid.cli.internal.FluidCommandLine.Companion.EXIT_CODE_SUCCESS
import io.redgreen.fluid.cli.internal.view.userMessage
import io.redgreen.fluid.cli.ui.Printer
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase.InstallationType.FRESH
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase.InstallationType.OVERWRITE
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase.Result.FreshInstallSuccessful
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase.Result.OverwriteSuccessful
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
    val candidate = validateGeneratorUseCase.invoke(candidatePath)
    return if (candidate is ValidGenerator) {
      val candidateGeneratorEntry = candidate.manifest.generator

      when (val lookupResult = lookupGeneratorUseCase.invoke(registry, candidate)) {
        NotInstalled -> performFreshInstall(candidate)

        AlreadyInstalled -> printAlreadyInstalledMessage(lookupResult as AlreadyInstalled, candidateGeneratorEntry.id)

        is DifferentHashes -> {
          printDifferentHashesMessage(lookupResult, candidateGeneratorEntry.id, candidateGeneratorEntry.version)
          confirmWithUser(
            { performOverwriteInstall(candidate) },
            this::printInstallationAborted
          )
        }

        is DifferentVersions -> {
          printDifferentVersionsMessage(lookupResult, candidateGeneratorEntry.id)
          confirmWithUser(
            { performOverwriteInstall(candidate) },
            this::printInstallationAborted
          )
        }
      }

      EXIT_CODE_SUCCESS
    } else {
      TODO("$candidate")
    }
  }

  private fun performFreshInstall(candidate: ValidGenerator) {
    val freshInstallSuccessful = installGeneratorUseCase.invoke(candidate, FRESH) as FreshInstallSuccessful
    printInstallSuccessfulMessage(freshInstallSuccessful, candidate.sha256)
  }

  private fun printInstallSuccessfulMessage(
    freshInstallSuccessful: FreshInstallSuccessful,
    hash: String
  ) {
    Printer.println { freshInstallSuccessful.userMessage(hash) }
  }

  private fun performOverwriteInstall(candidate: ValidGenerator) {
    val overwriteSuccessful = installGeneratorUseCase.invoke(candidate, OVERWRITE) as OverwriteSuccessful
    printInstallSuccessfulMessage(overwriteSuccessful, candidate.sha256)
  }

  private fun printInstallSuccessfulMessage(
    overwriteSuccessful: OverwriteSuccessful,
    hash: String
  ) {
    Printer.println { overwriteSuccessful.userMessage(hash) }
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

  private fun printInstallationAborted() {
    Printer.println { "Installation aborted." }
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
