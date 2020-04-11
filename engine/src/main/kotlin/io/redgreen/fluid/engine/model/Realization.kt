package io.redgreen.fluid.engine.model

sealed class Realization(
  open val path: String
)

data class DirectoryCreated(
  override val path: String
) : Realization(path)

data class FileCreated(
  override val path: String
) : Realization(path)
