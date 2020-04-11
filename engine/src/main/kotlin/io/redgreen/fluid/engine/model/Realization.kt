package io.redgreen.fluid.engine.model

sealed class Realization

data class DirectoryCreated(
  val path: String
) : Realization()

data class FileCreated(
  val path: String
) : Realization()
