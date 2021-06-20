package io.redgreen.fluid.engine.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeneratorEntry(
  val id: String,
  val implementation: String,
  val name: String,
  val description: String,
  val version: String
)
