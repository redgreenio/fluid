package io.redgreen.fluid

import io.redgreen.fluid.Snapshot.InMemory

object Fluid {
  fun createSnapshot(commands: List<Command>): Snapshot {
    commands.ifEmpty { throw IllegalArgumentException("'commands' should be a non-empty list.") }

    return InMemory().also {
      commands.onEach(it::execute)
    }
  }
}
