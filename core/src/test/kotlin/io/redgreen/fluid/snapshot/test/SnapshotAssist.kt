package io.redgreen.fluid.snapshot.test

import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Scaffold
import io.redgreen.fluid.snapshot.InMemorySnapshot

fun List<Command>.buildSnapshot(): InMemorySnapshot {
  val generatorClass = object : Generator {
    override fun scaffold(): Scaffold {
      val message = "This generator class is used only to test fetching resources " +
        "using the the class loader. This piece of code shouldn't be executed."
      throw IllegalStateException(message)
    }
  }::class.java

  return InMemorySnapshot.forGenerator(generatorClass).also { snapshot ->
    this.onEach { command ->
      when (command) {
        is DirectoryCommand -> snapshot.execute(command)
        is FileCommand -> snapshot.execute(command)
        is TemplateCommand<*> -> snapshot.execute(command)
      }
    }
  }
}

fun Command.buildSnapshot(): InMemorySnapshot =
  listOf(this).buildSnapshot()
