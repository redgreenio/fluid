package io.redgreen.fluid.api

import io.redgreen.fluid.Scaffold

interface Generator {
  fun scaffolds(): List<Scaffold>
}
