package io.redgreen.fluid.assist

import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.CopyDirectoryCommand
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.FileSystemEntry
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.TemplateCommand
import java.io.InputStream
import java.util.Optional

class CommandCapturingSnapshot : Snapshot {
  internal val capturedCommands = mutableListOf<Command>()

  override fun execute(command: DirectoryCommand) {
    capturedCommands.add(command)
  }

  override fun execute(command: FileCommand) {
    capturedCommands.add(command)
  }

  override fun <T : Any> execute(command: TemplateCommand<T>) {
    capturedCommands.add(command)
  }

  override fun execute(command: CopyDirectoryCommand) {
    capturedCommands.add(command)
  }

  override fun getEntries(): List<FileSystemEntry> {
    throw UnsupportedOperationException()
  }

  override fun inputStream(path: String): Optional<InputStream> {
    throw UnsupportedOperationException()
  }
}
