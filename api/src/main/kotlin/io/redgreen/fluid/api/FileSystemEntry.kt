package io.redgreen.fluid.api

sealed class FileSystemEntry(
  open val path: String
)

data class DirectoryEntry(
  override val path: String
) : FileSystemEntry(path)

data class FileEntry(
  override val path: String
) : FileSystemEntry(path)
