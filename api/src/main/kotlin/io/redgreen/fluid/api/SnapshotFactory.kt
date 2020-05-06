package io.redgreen.fluid.api

interface SnapshotFactory<S : Any> {
  fun newInstance(params: S): Snapshot
}
