package io.redgreen.fluid.cli.ui

object Printer {
  fun println(lazyMessage: () -> String) {
    println(lazyMessage().trim())
  }
}
