package io.redgreen.fluid.snapshot.test

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.dsl.Scaffold

class NoOpGenerator : Generator {
  override fun scaffold(): Scaffold {
    val message = "This generator class is used only to test fetching resources " +
      "using the the class loader. This piece of code shouldn't be executed."
    throw IllegalStateException(message)
  }
}
