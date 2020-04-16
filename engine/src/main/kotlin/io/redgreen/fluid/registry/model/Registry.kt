package io.redgreen.fluid.registry.model

import com.squareup.moshi.Moshi
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional

class Registry private constructor(val path: Path) {
  companion object {
    private const val FLUID_REGISTRY_DIR = ".fluid"
    private const val FLUID_GENERATORS_DIR = "libs"
    private const val REGISTRY_MANIFEST_FILE = "registry-manifest.json"

    fun from(path: Path): Registry =
      Registry(path.resolve(FLUID_REGISTRY_DIR))
  }

  private val moshi by lazy {
    Moshi.Builder().build()
  }

  val registryManifestPath: Path by lazy {
    this.path.resolve(REGISTRY_MANIFEST_FILE)
  }

  val artifactsPath: Path by lazy {
    this.path.resolve(FLUID_GENERATORS_DIR)
  }

  fun getRegistryEntry(generatorId: String): Optional<RegistryEntry> {
    if (!Files.exists(registryManifestPath)) {
      return Optional.empty()
    }

    val registryManifestJson = registryManifestPath.toFile().readText()
    val registryManifest = moshi.adapter(RegistryManifest::class.java).fromJson(registryManifestJson)
    val registryEntry = registryManifest
      ?.entries
      ?.find { it.id == generatorId }

    return registryEntry
      ?.let { Optional.of(it) }
      ?: Optional.empty()
  }
}
