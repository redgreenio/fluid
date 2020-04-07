package io.redgreen.fluid.snapshot.test

import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.snapshot.InMemorySnapshot

fun List<Command>.buildSnapshot(): InMemorySnapshot {
  return InMemorySnapshot().also { snapshot ->
    this.onEach { command ->
      when (command) {
        is DirectoryCommand -> snapshot.execute(command)
        is FileCommand -> snapshot.execute(command)
        is TemplateCommand<*> -> snapshot.execute(command)
      }
    }
  }
}

fun Command.buildSnapshot(): InMemorySnapshot {
  return listOf(this).buildSnapshot()
}
