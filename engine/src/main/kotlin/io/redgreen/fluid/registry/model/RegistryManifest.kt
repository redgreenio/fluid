package io.redgreen.fluid.registry.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistryManifest(
  val entries: List<RegistryEntry>
) {
  fun addEntry(entry: RegistryEntry): RegistryManifest =
    copy(entries = entries + entry)
}
