package io.redgreen.fluid.dsl

data class Resource(
  val filePath: String
) {
  companion object {
    val SAME_AS_DESTINATION = Resource("!திருக்குறள்!")
  }

  fun isSameAsDestination(): Boolean =
    this == SAME_AS_DESTINATION
}
