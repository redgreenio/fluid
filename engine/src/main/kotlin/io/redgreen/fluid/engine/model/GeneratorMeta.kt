package io.redgreen.fluid.engine.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeneratorMeta(
  val id: String,
  val implementation: String,
  val name: String,
  val description: String,
  val version: String
)
