package io.redgreen.fluid.registry.domain

import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import io.redgreen.fluid.registry.domain.AddRegistryEntryUseCase.Result.EntryAdded
import io.redgreen.fluid.registry.model.Registry
import io.redgreen.fluid.registry.model.RegistryEntry
import io.redgreen.fluid.registry.model.RegistryManifest
import okio.Buffer
import java.nio.file.Files
import java.nio.file.Path

class AddRegistryEntryUseCase(
  private val registry: Registry,
  private val moshi: Moshi
) {
  private val registryManifestAdapter by lazy { // TODO Move all this logic into Registry?
    moshi.adapter(RegistryManifest::class.java)
  }

  fun invoke(entry: RegistryEntry): Result {
    val manifestPath = registry.registryManifestPath
    val registryManifest = createOrUpdateRegistryManifest(manifestPath, entry)
    writeToFile(manifestPath, registryManifest)
    return EntryAdded
  }

  private fun createOrUpdateRegistryManifest(
    manifestFilePath: Path,
    entry: RegistryEntry
  ): RegistryManifest {
    val registryFileExists = Files.exists(registry.path)
      && Files.exists(manifestFilePath)

    return if (registryFileExists) {
      addEntryToRegistryManifest(manifestFilePath, entry)
    } else {
      createRegistryManifest(entry)
    }
  }

  private fun addEntryToRegistryManifest(
    registryManifestPath: Path,
    entry: RegistryEntry
  ): RegistryManifest {
    val registryJson = registryManifestPath.toFile().readText()
    return try {
      val registry = registryManifestAdapter.fromJson(registryJson)!!
      registry.addEntry(entry)
    } catch (exception: JsonEncodingException) {
      // Corrupt registry
      createRegistryManifest(entry)
    }
  }

  private fun createRegistryManifest(
    entry: RegistryEntry
  ): RegistryManifest =
    RegistryManifest(listOf(entry))

  private fun writeToFile(
    registryManifestPath: Path,
    registryManifest: RegistryManifest
  ) {
    val buffer = Buffer()
    val jsonWriter = JsonWriter.of(buffer).apply { indent = "  " }
    registryManifestAdapter.toJson(jsonWriter, registryManifest)
    val registryJsonContents = buffer.readUtf8()

    if (!Files.exists(registryManifestPath)) {
      createNewRegistryManifestFile(registryManifestPath)
    }

    registryManifestPath.toFile().writeText(registryJsonContents)
  }

  private fun createNewRegistryManifestFile(
    registryManifestPath: Path
  ) {
    Files.createDirectories(registryManifestPath.parent)
    Files.createFile(registryManifestPath)
  }

  sealed class Result {
    object EntryAdded : Result()
  }
}
