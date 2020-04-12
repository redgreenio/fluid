package io.redgreen.fluid.cli.internal

import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.io.File
import java.util.concurrent.Callable

@Command(name = "run")
class RunCommand : Callable<Int> {
  @Parameters(index = "0")
  internal lateinit var generatorJar: String

  @Parameters(index = "1")
  internal lateinit var destination: File

  override fun call(): Int {
    println("$generatorJar ${destination.absolutePath}")
    return 0
  }
}
