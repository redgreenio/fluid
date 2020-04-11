package io.redgreen.fluid.engine

import io.redgreen.fluid.api.DirectoryEntry
import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.api.FileSystemEntry
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.engine.model.DirectoryCreated
import io.redgreen.fluid.engine.model.FileCreated
import io.redgreen.fluid.engine.model.Realization
import java.io.File
import java.nio.file.Files

internal class Realizer {
  fun realize(
    destinationRoot: File,
    snapshot: Snapshot
  ) : List<Realization> {
    return snapshot
      .getEntries()
      .onEach { entry -> writeToDisk(destinationRoot, snapshot, entry) }
      .map(this::toRealization)
  }

  private fun writeToDisk(
    destinationRoot: File,
    snapshot: Snapshot,
    entry: FileSystemEntry
  ) {
    when (entry) {
      is DirectoryEntry -> createDirectory(destinationRoot, entry)
      is FileEntry -> copyFile(destinationRoot, snapshot, entry)
    }
  }

  private fun toRealization(entry: FileSystemEntry): Realization {
    return when (entry) {
      is DirectoryEntry -> DirectoryCreated(entry.path)
      is FileEntry -> FileCreated(entry.path)
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
