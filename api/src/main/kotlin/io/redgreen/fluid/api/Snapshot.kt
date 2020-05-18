package io.redgreen.fluid.api

import java.io.InputStream
import java.util.Optional

/**
 * A snapshot is a temporary holding area for the files and directories (currently in-memory) before they
 * are written to a persistent file system.
 */
interface Snapshot {
  /**
   * Execute the [DirectoryCommand] and create an entity representing a directory.
   *
   * @param command the directory command.
   */
  fun execute(command: DirectoryCommand)

  /**
   * Execute the [FileCommand] and create an entity representing the target file.
   *
   * @param command the file command.
   */
  fun execute(command: FileCommand)

  /**
   * Execute the [TemplateCommand] and create an entity representing the target template.
   *
   * @param M model rendered by the template.
   * @param command the template command.
   */
  fun <M : Any> execute(command: TemplateCommand<M>)

  /**
   * Execute the [CopyDirectoryCommand] and create an entity or entities representing the target directory.
   *
   * @param command the copy directory command.
   */
  fun execute(command: CopyDirectoryCommand)

  /**
   * Get the list of [FileSystemEntry] objects in the [Snapshot].
   *
   * @return list of [FileSystemEntry] objects.
   */
  fun getEntries(): List<FileSystemEntry>

  /**
   * Opens an [InputStream] to the specified file path.
   *
   * @param path a file path in the snapshot.
   * @return An [InputStream] or [Optional.empty] if a stream cannot be opened.
   */
  fun inputStream(path: String): Optional<InputStream>
}
