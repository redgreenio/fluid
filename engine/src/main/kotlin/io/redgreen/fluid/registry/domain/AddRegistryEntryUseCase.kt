package io.redgreen.fluid.registry.domain

import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import io.redgreen.fluid.registry.domain.AddRegistryEntryUseCase.Result.EntryAdded
import io.redgreen.fluid.registry.model.Registry
import io.redgreen.fluid.registry.model.RegistryEntry
import io.redgreen.fluid.registry.model.RegistryHome
import okio.Buffer
import java.nio.file.Files
import java.nio.file.Path

class AddRegistryEntryUseCase(
  private val registryHome: RegistryHome,
  private val moshi: Moshi
) {
  companion object {
    private const val REGISTRY_FILE = "registry.json"
  }

  private val registryAdapter by lazy {
    moshi.adapter(Registry::class.java)
  }

  fun invoke(entry: RegistryEntry): Result {
    val registryFilePath = registryHome.path.resolve(REGISTRY_FILE)
    val registry = createOrUpdateRegistry(registryFilePath, entry)
    writeRegistryToFile(registryFilePath, registry)
    return EntryAdded
  }

  private fun createOrUpdateRegistry(
    registryFilePath: Path,
    entry: RegistryEntry
  ): Registry {
    val registryFileExists = Files.exists(registryHome.path)
      && Files.exists(registryFilePath)

    return if (registryFileExists) {
      getUpdatedRegistry(registryFilePath, entry)
    } else {
      createNewRegistryFile(registryFilePath)
      getNewRegistry(entry)
    }
  }

  private fun getUpdatedRegistry(
    registryFilePath: Path,
    entry: RegistryEntry
  ): Registry {
    val registryJson = Files.readString(registryFilePath)
    return try {
      val registry = registryAdapter.fromJson(registryJson)!!
      registry.addEntry(entry)
    } catch (exception: JsonEncodingException) {
      // Corrupt registry
      getNewRegistry(entry)
    }
  }

  private fun createNewRegistryFile(registryFilePath: Path) {
    Files.createDirectories(registryFilePath.parent)
    Files.createFile(registryFilePath)
  }

  private fun getNewRegistry(entry: RegistryEntry): Registry =
    Registry(listOf(entry))

  private fun writeRegistryToFile(registryFilePath: Path, registry: Registry) {
    val buffer = Buffer()
    val jsonWriter = JsonWriter.of(buffer).apply { indent = "  " }
    registryAdapter.toJson(jsonWriter, registry)
    val registryJsonContents = buffer.readUtf8()

    registryFilePath.toFile().writeText(registryJsonContents)
  }

  sealed class Result {
    object EntryAdded : Result()
  }
}
