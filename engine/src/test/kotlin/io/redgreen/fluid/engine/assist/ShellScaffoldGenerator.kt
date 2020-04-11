package io.redgreen.fluid.engine.assist

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.dsl.Scaffold

class ShellScaffoldGenerator(
  private val scaffold: Scaffold
) : Generator {
  override fun scaffold(): Scaffold =
    scaffold
}
