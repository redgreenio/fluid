package io.redgreen.fluid.snapshot

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.SnapshotFactory

class InMemorySnapshotFactory : SnapshotFactory<Class<out Generator<*>>> {
  override fun newInstance(params: Class<out Generator<*>>): Snapshot =
    InMemorySnapshot.forGenerator(params)
}
