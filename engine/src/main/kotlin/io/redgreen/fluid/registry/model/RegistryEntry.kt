package io.redgreen.fluid.registry.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistryEntry(
  val generatorPath: String
)
