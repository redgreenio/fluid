package io.redgreen.fluid.engine.model

import io.redgreen.fluid.api.Generator
import java.nio.file.Path

data class GeneratorJar internal constructor(
  val path: Path,
  val generatorClass: Class<out Generator>
)
