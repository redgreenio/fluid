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

    val registryManifestOptional = getRegistryManifest()
    if (!registryManifestOptional.isPresent) {
      return Optional.empty<RegistryEntry>()
    }

    val maybeRegistryEntry = registryManifestOptional.get()
      .entries
      .find { it.id == generatorId }

    return maybeRegistryEntry
      ?.let { Optional.of(it) }
      ?: Optional.empty()
  }

  fun update(entry: RegistryEntry) {
    writeManifestFile(updateEntryInRegistryManifest(entry))
  }

  fun getEntries(): List<RegistryEntry> {
    val registryManifestOptional = getRegistryManifest()
    return if (!registryManifestOptional.isPresent) {
      emptyList()
    } else {
      registryManifestOptional.get().entries
    }
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
    val registryManifestOptional = getRegistryManifest()
    return try {
      if (!registryManifestOptional.isPresent) {
        createRegistryManifest(entry)
      } else {
        registryManifestOptional.get().addEntry(entry)
      }
    } catch (exception: JsonEncodingException) {
      // Corrupt registry
      createRegistryManifest(entry)
    }
  }

  private fun getRegistryManifest(): Optional<RegistryManifest> {
    if (!Files.exists(registryManifestPath)) {
      return Optional.empty()
    }

    val maybeRegistryManifestJson = registryManifestPath.toFile().readText()
    return try {
      val registryManifest = registryManifestAdapter.fromJson(maybeRegistryManifestJson)!!
      Optional.of(registryManifest)
    } catch (exception: JsonEncodingException) {
      // Corrupt registry
      Optional.empty()
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
  ): RegistryManifest =
    getRegistryManifest().get().updateEntry(entry)
}
