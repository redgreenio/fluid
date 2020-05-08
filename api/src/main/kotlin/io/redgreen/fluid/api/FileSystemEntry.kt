package io.redgreen.fluid.api

import io.redgreen.fluid.dsl.Permission.READ_WRITE

sealed class FileSystemEntry(
  open val path: String
)

data class DirectoryEntry(
  override val path: String
) : FileSystemEntry(path)

data class FileEntry(
  override val path: String,
  val permissions: Int = READ_WRITE
) : FileSystemEntry(path)
