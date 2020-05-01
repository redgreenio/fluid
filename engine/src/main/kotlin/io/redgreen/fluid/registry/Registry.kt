package io.redgreen.fluid.registry

import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import io.redgreen.fluid.registry.model.RegistryEntry
import io.redgreen.fluid.registry.model.RegistryManifest
import okio.Buffer
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional

class Registry private constructor(
  internal val root: Path
) {
  companion object {
    private const val FLUID_REGISTRY_DIR = ".fluid"
    private const val FLUID_GENERATORS_DIR = "libs"
    private const val REGISTRY_MANIFEST_FILE = "registry-manifest.json"
    private const val JSON_INDENT = "  "

    fun from(userHome: Path): Registry =
      Registry(userHome.resolve(FLUID_REGISTRY_DIR))
  }

  val registryManifestPath: Path by lazy { root.resolve(REGISTRY_MANIFEST_FILE) }
  val artifactsPath: Path by lazy { root.resolve(FLUID_GENERATORS_DIR) }

  private val moshi by lazy { Moshi.Builder().build() }
  private val registryManifestAdapter by lazy { moshi.adapter(RegistryManifest::class.java) }

  fun add(entry: RegistryEntry) {
    val registryEntryIsCorruptRegistryPair = try {
      getEntryById(entry.id) to false
    } catch (exception: JsonEncodingException) {
      Optional.empty<RegistryEntry>() to true
    }

    val (registryEntryOptional, isCorruptRegistry) = registryEntryIsCorruptRegistryPair
    val entryExists = registryEntryOptional.isPresent && !isCorruptRegistry

    if (entryExists) {
      update(entry)
    } else {
      val newRegistryManifest = createOrUpdateRegistryManifest(entry)
      writeManifestFile(newRegistryManifest)
    }
  }

  fun getEntryById(generatorId: String): Optional<RegistryEntry> {
    if (!Files.exists(registryManifestPath)) {
      return Optional.empty()
    }

    val registryManifestJson = registryManifestPath.toFile().readText()
    val registryManifest = registryManifestAdapter.fromJson(registryManifestJson)
    val registryEntry = registryManifest
      ?.entries
      ?.find { it.id == generatorId }

    return registryEntry
      ?.let { Optional.of(it) }
      ?: Optional.empty()
  }

  fun update(entry: RegistryEntry) {
    writeManifestFile(updateEntryInRegistryManifest(entry))
  }

  private fun createOrUpdateRegistryManifest(
    entry: RegistryEntry
  ): RegistryManifest {
    val registryFileExists = Files.exists(root)
      && Files.exists(registryManifestPath)

    return if (registryFileExists) {
      addEntryToRegistryManifest(entry)
    } else {
      createRegistryManifest(entry)
    }
  }

  private fun addEntryToRegistryManifest(
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

  private fun writeManifestFile(
    registryManifest: RegistryManifest
  ) {
    val buffer = Buffer()
    val jsonWriter = JsonWriter.of(buffer).apply { indent = JSON_INDENT }
    registryManifestAdapter.toJson(jsonWriter, registryManifest)
    val registryManifestJson = buffer.readUtf8()

    if (!Files.exists(registryManifestPath)) {
      createNewRegistryManifestFile()
    }

    registryManifestPath.toFile().writeText(registryManifestJson)
  }

  private fun createNewRegistryManifestFile() {
    Files.createDirectories(registryManifestPath.parent)
    Files.createFile(registryManifestPath)
  }

  private fun updateEntryInRegistryManifest(
    entry: RegistryEntry
  ): RegistryManifest {
    val registryJson = registryManifestPath.toFile().readText()
    val registryManifest = registryManifestAdapter.fromJson(registryJson)!!
    return registryManifest.updateEntry(entry)
  }
}
