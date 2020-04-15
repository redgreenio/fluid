package io.redgreen.fluid.cli.internal

import com.squareup.moshi.Moshi
import io.redgreen.fluid.cli.internal.FluidCommandLine.Companion.EXIT_CODE_SUCCESS
import io.redgreen.fluid.cli.ui.Printer
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.ValidGenerator
import io.redgreen.fluid.registry.domain.InstallGeneratorUseCase
import io.redgreen.fluid.registry.model.RegistryHome
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Path
import java.util.concurrent.Callable

@Command(name = "install")
internal class InstallCommand(
  private val userHomeDir: Path
) : Callable<Int> {
  @Option(names = ["-j", "--jar"])
  internal lateinit var artifactPath: Path

  private val validateGeneratorJarUseCase by lazy {
    ValidateGeneratorJarUseCase()
  }

  private val installGeneratorJarUseCase by lazy {
    InstallGeneratorUseCase(RegistryHome.from(userHomeDir), Moshi.Builder().build())
  }

  override fun call(): Int {
    val result = validateGeneratorJarUseCase.invoke(artifactPath)
    return if (result is ValidGenerator) {
      installGeneratorJarUseCase.invoke(result)
      Printer.print { "Digest: sha256:${result.sha256}" }
      Printer.print { "Installed generator '${result.manifest.generator.id}' from '${result.artifactPath}'" }
      EXIT_CODE_SUCCESS
    } else {
      -1
    }
  }
}
