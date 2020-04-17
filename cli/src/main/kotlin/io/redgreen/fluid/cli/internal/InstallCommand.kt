package io.redgreen.fluid.cli.internal

import com.squareup.moshi.Moshi
import io.redgreen.fluid.cli.internal.FluidCommandLine.Companion.EXIT_CODE_SUCCESS
import io.redgreen.fluid.cli.ui.Printer
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.AlreadyInstalled
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentHashes
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentVersions
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.NotInstalled
import io.redgreen.fluid.registry.model.Registry
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Path
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
    val validateGeneratorResult = validateGeneratorUseCase.invoke(candidatePath)
    return if (validateGeneratorResult is ValidGenerator) {
      when (val lookupResult = lookupGeneratorUseCase.invoke(registry, validateGeneratorResult)) {
        NotInstalled -> performFreshInstall(validateGeneratorResult)
        AlreadyInstalled -> printAlreadyInstalledMessage()
        is DifferentHashes -> println(lookupResult)
        is DifferentVersions -> println(lookupResult)
      }

      EXIT_CODE_SUCCESS
    } else {
      TODO("Not a valid generator")
    }
  }

  private fun performFreshInstall(validGenerator: ValidGenerator) {
    installGeneratorUseCase.invoke(validGenerator)
    printFreshInstallMessage(validGenerator)
  }

  private fun printFreshInstallMessage(validGenerator: ValidGenerator) {
    Printer.print { "Digest: sha256:${validGenerator.sha256}" }
    Printer.print { "Installed generator '${validGenerator.manifest.generator.id}' from '${validGenerator.artifactPath}'" }
  }

  private fun printAlreadyInstalledMessage() {
    Printer.print { "Already installed. No changes were made." }
  }
}
