package io.redgreen.fluid.registry.model

import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import okio.Buffer
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional

class Registry private constructor(val path: Path) { // TODO make `path` private?
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

  private val registryManifestAdapter by lazy {
    moshi.adapter(RegistryManifest::class.java)
  }

  val registryManifestPath: Path by lazy {
    this.path.resolve(REGISTRY_MANIFEST_FILE)
  }

  val artifactsPath: Path by lazy {
    this.path.resolve(FLUID_GENERATORS_DIR)
  }

  fun add(entry: RegistryEntry) {
    val registryManifest = createOrUpdateRegistryManifest(registryManifestPath, entry)
    writeToFile(path.resolve(REGISTRY_MANIFEST_FILE), registryManifest)
  }

  fun update(entry: RegistryEntry) {
    val manifestPath = registryManifestPath
    val updatedRegistryManifest = updateEntryInRegistryManifest(manifestPath, entry)
    writeToFile(manifestPath, updatedRegistryManifest)
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

  private fun createOrUpdateRegistryManifest(
    manifestFilePath: Path,
    entry: RegistryEntry
  ): RegistryManifest {
    val registryFileExists = Files.exists(path)
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


  private fun updateEntryInRegistryManifest(
    registryManifestPath: Path,
    entry: RegistryEntry
  ): RegistryManifest {
    val registryJson = registryManifestPath.toFile().readText()
    val registryManifest = registryManifestAdapter.fromJson(registryJson)!!
    return registryManifest.updateEntry(entry)
  }
}
