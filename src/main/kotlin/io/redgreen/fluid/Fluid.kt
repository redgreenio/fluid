package io.redgreen.fluid

import io.redgreen.fluid.Snapshot.Empty
import io.redgreen.fluid.Snapshot.InMemory
import io.redgreen.fluid.commands.Command

object Fluid {
  fun createSnapshot(commands: List<Command>): Snapshot {
    if (commands.isEmpty()) return Empty

    return InMemory().also {
      commands.onEach(it::execute)
    }
  }
}
