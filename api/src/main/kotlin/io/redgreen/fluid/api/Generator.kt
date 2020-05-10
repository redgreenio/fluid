package io.redgreen.fluid.api

import io.redgreen.fluid.dsl.Scaffold

interface Generator<C : Any> {
  fun configure(): C
  fun scaffold(): Scaffold
}
