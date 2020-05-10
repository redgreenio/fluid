package io.redgreen.fluid.snapshot.assist

import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.snapshot.InMemorySnapshot

fun List<Command>.buildSnapshot(): Snapshot {
  val generatorClass = NoOpGenerator::class.java.asSubclass(Generator::class.java)

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

fun Command.buildSnapshot(): Snapshot =
  listOf(this).buildSnapshot()
