package io.redgreen.fluid.dsl

/**
 * Provides an entry point into the Fluid DSL. This function is the only public API
 * to create an instance of the [Scaffold] object.
 */
fun <C : Any> scaffold(
  block: Scaffold<C>.(C) -> Unit
): Scaffold<C> =
  Scaffold(block)
