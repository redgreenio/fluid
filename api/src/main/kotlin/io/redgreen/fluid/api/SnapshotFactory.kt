package io.redgreen.fluid.api

/**
 * A factory contract that enables us to create instances of a given [Snapshot] implementation.
 *
 * @param S information required to construct a [Snapshot] object.
 */
interface SnapshotFactory<S : Any> {
  /**
   * An instance creator factory function to create a [Snapshot] instance.
   *
   * @param params the params object.
   */
  fun newInstance(params: S): Snapshot
}
