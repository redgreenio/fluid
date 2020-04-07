package io.redgreen.fluid

import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.snapshot.InMemorySnapshot

object Fluid {
  fun createSnapshot(commands: List<Command>): Snapshot {
    commands.ifEmpty { throw IllegalArgumentException("'commands' should be a non-empty list.") }

    return InMemorySnapshot().also { snapshot ->
      commands.onEach { command ->
        when (command) {
          is DirectoryCommand -> snapshot.execute(command)
          is FileCommand -> snapshot.execute(command)
          is TemplateCommand<*> -> snapshot.execute(command)
        }
      }
    }
  }
}
