package io.redgreen.fluid.registry.model

import java.nio.file.Path

class RegistryHome private constructor(val path: Path) {
  companion object {
    private const val FLUID_HOME_DIR = ".fluid"
    private const val FLUID_GENERATORS_DIR = "libs"
    private const val REGISTRY_MANIFEST_FILE = "registry-manifest.json"

    fun from(userHomeDir: Path): RegistryHome =
      RegistryHome(userHomeDir.resolve(FLUID_HOME_DIR))
  }

  val registryManifestPath: Path by lazy {
    this.path.resolve(REGISTRY_MANIFEST_FILE)
  }

  val artifactsPath: Path by lazy {
    this.path.resolve(FLUID_GENERATORS_DIR)
  }
}
