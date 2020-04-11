package io.redgreen.fluid.engine

import io.redgreen.fluid.api.DirectoryEntry
import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.api.Snapshot
import java.io.File
import java.nio.file.Files

class Realizer {
  fun realize(
    destinationRoot: File,
    snapshot: Snapshot
  ) {
    snapshot.getEntries().onEach { entry ->
      when (entry) {
        is DirectoryEntry -> createDirectory(destinationRoot, entry)
        is FileEntry -> copyFile(destinationRoot, snapshot, entry)
      }
    }
  }

  private fun createDirectory(
    destinationRoot: File,
    entry: DirectoryEntry
  ) {
    destinationRoot.resolve(entry.path).mkdirs()
  }

  private fun copyFile(
    destinationRoot: File,
    snapshot: Snapshot,
    entry: FileEntry
  ) {
    val target = destinationRoot.resolve(entry.path)
    with(target) {
      parentFile.mkdirs()
    }
    Files.copy(snapshot.inputStream(entry.path).get(), target.toPath())
  }
}
