package io.redgreen.fluid.dsl

/**
 * Class used to explicitly specify the source of files and directories in the project's
 * **resources** directory.
 *
 * @property path file or directory path.
 */
data class Source(
  val path: String
) {
  companion object {
    /**
     * Sentinel value used when the source path is same as the destination path.
     */
    val MIRROR_DESTINATION = Source("!திருக்குறள்!")
  }

  /**
   * A handy boolean check to check if the source and destination paths are the same.
   */
  val mirrorsDestination: Boolean by lazy { this == MIRROR_DESTINATION }
}
