package io.redgreen.fluid.registry.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistryManifest(
  val entries: List<RegistryEntry>
) {
  fun addEntry(entry: RegistryEntry): RegistryManifest =
    copy(entries = entries + entry)

  fun updateEntry(updatedEntry: RegistryEntry): RegistryManifest {
    val indexOfEntry = entries.indexOfFirst { it.id == updatedEntry.id }
    val updatedEntries = entries
      .mapIndexed { index, registryEntry ->
        if (index == indexOfEntry) updatedEntry else registryEntry
      }
    return RegistryManifest(updatedEntries)
  }
}
