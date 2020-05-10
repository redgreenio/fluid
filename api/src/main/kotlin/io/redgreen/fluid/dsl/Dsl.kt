package io.redgreen.fluid.dsl

fun <C : Any> scaffold(
  block: Scaffold<C>.(C) -> Unit
): Scaffold<C> =
  Scaffold(block)
