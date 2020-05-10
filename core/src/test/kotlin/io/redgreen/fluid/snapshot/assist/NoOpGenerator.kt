package io.redgreen.fluid.snapshot.assist

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.dsl.Scaffold

class NoOpGenerator : Generator<Unit> {
  override fun configure() {
    /* This DslConfig is a `Unit` */
  }

  override fun scaffold(): Scaffold<Unit> {
    val message = "This generator class is used only to test fetching resources " +
      "using the the class loader. This piece of code shouldn't be executed."
    throw IllegalStateException(message)
  }
}
