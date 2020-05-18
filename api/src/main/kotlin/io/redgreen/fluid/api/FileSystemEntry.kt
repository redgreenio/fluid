package io.redgreen.fluid.api

import io.redgreen.fluid.dsl.Permission.READ_WRITE

/**
 * File system entries represent actual entries that are present in [Snapshot] implementations.
 *
 * @property path a file system entry path.
 */
sealed class FileSystemEntry(
  open val path: String
)

/**
 * Represents a directory in the [Snapshot] implementation.
 *
 * @property path directory path.
 */
data class DirectoryEntry(
  override val path: String
) : FileSystemEntry(path)

/**
 * Represents a file in the [Snapshot] implementation.
 *
 * @property path file path.
 * @property permissions the file's permissions. Defaults to [READ_WRITE].
 */
data class FileEntry(
  override val path: String,
  val permissions: Int = READ_WRITE
) : FileSystemEntry(path)
