package io.redgreen.fluid.registry.domain

import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import io.redgreen.fluid.registry.domain.UpdateRegistryUseCase.Result.EntryUpdated
import io.redgreen.fluid.registry.model.Registry
import io.redgreen.fluid.registry.model.RegistryEntry
import io.redgreen.fluid.registry.model.RegistryManifest
import okio.Buffer
import java.nio.file.Path

class UpdateRegistryUseCase(
  val registry: Registry,
  val moshi: Moshi
) {
  private val registryManifestAdapter by lazy { // TODO Move all this logic into Registry?
    moshi.adapter(RegistryManifest::class.java)
  }

  fun invoke(registryEntry: RegistryEntry): Result {
    val manifestPath = registry.registryManifestPath
    val updatedRegistryManifest = updateEntryInRegistryManifest(manifestPath, registryEntry)
    writeToFile(manifestPath, updatedRegistryManifest)
    return EntryUpdated
  }

  private fun updateEntryInRegistryManifest(
    registryManifestPath: Path,
    entry: RegistryEntry
  ): RegistryManifest {
    val registryJson = registryManifestPath.toFile().readText()
    val registryManifest = registryManifestAdapter.fromJson(registryJson)!!
    return registryManifest.updateEntry(entry)
  }

  private fun writeToFile(
    registryManifestPath: Path,
    registryManifest: RegistryManifest
  ) {
    val buffer = Buffer()
    val jsonWriter = JsonWriter.of(buffer).apply { indent = "  " }
    registryManifestAdapter.toJson(jsonWriter, registryManifest)
    val registryJsonContents = buffer.readUtf8()

    registryManifestPath.toFile().writeText(registryJsonContents)
  }

  sealed class Result {
    object EntryUpdated : Result()
  }
}
