package io.redgreen.fluid.registry.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Registry(
  val entries: List<RegistryEntry>
) {
  fun addEntry(entry: RegistryEntry): Registry =
    copy(entries = entries + entry)
}
