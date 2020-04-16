package io.redgreen.fluid.engine.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MaintainerEntry(
  val name: String,
  val website: String,
  val email: String
)
