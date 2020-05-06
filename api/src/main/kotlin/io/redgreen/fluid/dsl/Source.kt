package io.redgreen.fluid.dsl

data class Source(
  val path: String
) {
  companion object {
    val MIRROR_DESTINATION = Source("!திருக்குறள்!")
  }

  val mirrorsDestination: Boolean by lazy { this == MIRROR_DESTINATION }
}
