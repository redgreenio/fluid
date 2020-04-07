package io.redgreen.fluid.api

import io.redgreen.fluid.dsl.Scaffold

interface Generator {
  fun scaffold(): Scaffold
}
