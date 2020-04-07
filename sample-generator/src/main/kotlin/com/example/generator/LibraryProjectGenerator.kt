package com.example.generator

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.dsl.Scaffold

class LibraryProjectGenerator : Generator {
  override fun scaffold(): Scaffold {
    return libraryProjectScaffold
  }
}
