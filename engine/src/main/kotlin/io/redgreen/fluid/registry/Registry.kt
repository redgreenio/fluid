package io.redgreen.fluid.registry

import io.redgreen.fluid.registry.model.RegistryEntry
import java.nio.file.Path
import java.util.Optional

interface Registry {
  val root: Path
  val registryManifestPath: Path
  val artifactsPath: Path

  fun add(entry: RegistryEntry)
  fun getEntryById(generatorId: String): Optional<RegistryEntry>
  fun update(entry: RegistryEntry)
  fun getEntries(): List<RegistryEntry>
}
