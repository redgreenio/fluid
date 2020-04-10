package io.redgreen.fluid.api

import java.io.InputStream
import java.util.Optional

interface Snapshot {
  fun execute(command: DirectoryCommand)
  fun execute(command: FileCommand)
  fun <T : Any> execute(command: TemplateCommand<T>)
  fun getEntries(): List<FileSystemEntry>
  fun inputStream(path: String): Optional<InputStream>
}
