package io.redgreen.fluid.cli.ui

object Printer {
  fun print(lazyMessage: () -> String) {
    println(lazyMessage())
  }
}
