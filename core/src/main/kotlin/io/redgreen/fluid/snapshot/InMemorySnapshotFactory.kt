package io.redgreen.fluid.snapshot

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.SnapshotFactory

class InMemorySnapshotFactory : SnapshotFactory<Class<Generator>> {
  override fun newInstance(param: Class<Generator>): Snapshot =
    InMemorySnapshot.forGenerator(param)
}
