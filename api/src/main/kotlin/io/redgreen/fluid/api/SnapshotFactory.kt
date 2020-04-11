package io.redgreen.fluid.api

interface SnapshotFactory<T : Any> {
  fun newInstance(param: T): Snapshot
}
