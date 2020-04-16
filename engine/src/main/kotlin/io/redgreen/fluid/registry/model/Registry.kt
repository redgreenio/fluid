package io.redgreen.fluid.registry.model

import java.nio.file.Path

class Registry private constructor(val path: Path) {
  companion object {
    private const val FLUID_REGISTRY_DIR = ".fluid"
    private const val FLUID_GENERATORS_DIR = "libs"
    private const val REGISTRY_MANIFEST_FILE = "registry-manifest.json"

    fun from(path: Path): Registry =
      Registry(path.resolve(FLUID_REGISTRY_DIR))
  }

  val registryManifestPath: Path by lazy {
    this.path.resolve(REGISTRY_MANIFEST_FILE)
  }

  val artifactsPath: Path by lazy {
    this.path.resolve(FLUID_GENERATORS_DIR)
  }
}
