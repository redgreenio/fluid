package io.redgreen.fluid

import io.redgreen.fluid.snapshot.InMemorySnapshot
import io.redgreen.fluid.snapshot.Snapshot

object Fluid {
  fun createSnapshot(commands: List<Command>): Snapshot {
    commands.ifEmpty { throw IllegalArgumentException("'commands' should be a non-empty list.") }

    return InMemorySnapshot().also {
      commands.onEach(it::execute)
    }
  }
}
