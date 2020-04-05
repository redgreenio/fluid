package io.redgreen.fluid

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.redgreen.fluid.commands.Command
import io.redgreen.fluid.commands.DirectoryCommand
import io.redgreen.fluid.commands.FileCopyCommand
import io.redgreen.fluid.commands.TemplateCommand
import java.nio.file.FileSystem
import java.nio.file.Files

sealed class Snapshot {
  object Empty : Snapshot()

  class InMemory private constructor(
    fileSystem: FileSystem
  ) : Snapshot() {
    private val root = fileSystem.getPath("")

    constructor(): this(Jimfs.newFileSystem(Configuration.unix()))

    fun directoryExists(path: String): Boolean {
      val resolvedPath = root.resolve(path)
      return Files.exists(resolvedPath) && Files.isDirectory(resolvedPath)
    }

    internal fun execute(command: Command) {
      when(command) {
        is DirectoryCommand -> createDirectory(command.path)
        is FileCopyCommand -> TODO()
        is TemplateCommand<*> -> TODO()
      }
    }

    // TODO(rj) 6-Apr-20 Return a result sealed class with success and failure
    private fun createDirectory(path: String) {
      Files.createDirectory(root.resolve(path))
    }
  }
}
