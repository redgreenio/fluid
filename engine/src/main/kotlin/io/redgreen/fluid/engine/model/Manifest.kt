package io.redgreen.fluid.engine.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Manifest(
  val generator: GeneratorEntry,
  val maintainer: MaintainerEntry
)
