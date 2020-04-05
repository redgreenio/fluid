package io.redgreen.fluid.dsl

fun scaffold(block: Scaffold.() -> Unit): Scaffold =
  Scaffold(block)
