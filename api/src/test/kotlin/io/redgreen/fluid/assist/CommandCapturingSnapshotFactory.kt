package io.redgreen.fluid.assist

import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.SnapshotFactory

class CommandCapturingSnapshotFactory : SnapshotFactory<Unit> {
  override fun newInstance(param: Unit): Snapshot =
    CommandCapturingSnapshot()
}
