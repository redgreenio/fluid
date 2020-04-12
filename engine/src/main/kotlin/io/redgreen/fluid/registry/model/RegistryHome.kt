package io.redgreen.fluid.registry.model

import java.nio.file.Path

data class RegistryHome(val path: Path) {
  companion object {
    private const val FLUID_HOME_DIR = ".fluid"

    fun from(userHomeDir: Path): RegistryHome =
      RegistryHome(userHomeDir.resolve(FLUID_HOME_DIR))
  }
}
