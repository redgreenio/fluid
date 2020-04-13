package io.redgreen.fluid.registry.model

import java.nio.file.Path

class RegistryHome private constructor(val path: Path) {
  companion object {
    private const val FLUID_HOME_DIR = ".fluid"
    private const val FLUID_GENERATORS_DIR = "libs"
    private const val REGISTRY_FILE_NAME = "registry.json"

    fun from(userHomeDir: Path): RegistryHome =
      RegistryHome(userHomeDir.resolve(FLUID_HOME_DIR))
  }

  val registryFilePath: Path by lazy {
    this.path
      .resolve(REGISTRY_FILE_NAME)
  }

  fun artifactPath(artifactFileName: String): Path {
    return this.path
      .resolve(FLUID_GENERATORS_DIR)
      .resolve(artifactFileName)
  }
}
