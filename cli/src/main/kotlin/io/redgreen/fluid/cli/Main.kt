package io.redgreen.fluid.cli

import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.ValidGenerator
import io.redgreen.fluid.engine.domain.RunGeneratorUseCase
import java.io.File

fun main(args: Array<String>) {
  val result = LoadGeneratorJarUseCase().invoke(args.first())
  if (result !is ValidGenerator) {
    TODO("[${result::class.java.simpleName}] Print appropriate error message!")
  } else {
    val destinationDir = File(args.last())
    if (!destinationDir.exists()) {
      if (!destinationDir.mkdirs()) {
        TODO("Unable to create directory: ${destinationDir.absolutePath}")
      }
    }
    val realizations = RunGeneratorUseCase().invoke(result.generatorClass, destinationDir).realizations
    for (realization in realizations) {
      println(realization.path)
    }
  }
}
