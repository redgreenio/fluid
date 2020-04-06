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

    fun fileExists(path: String): Boolean {
      val resolvedPath = root.resolve(path)
      return Files.exists(resolvedPath) && !Files.isDirectory(resolvedPath)
    }

    internal fun execute(command: Command) {
      when(command) {
        is DirectoryCommand -> createDirectory(command.path)
        is FileCopyCommand -> copyFile(command.filePath)
        is TemplateCommand<*> -> TODO()
      }
    }

    // TODO(rj) 6-Apr-20 Return a result sealed class with success and failure
    private fun createDirectory(path: String) {
      Files.createDirectories(root.resolve(path))
    }

    private fun copyFile(filePath: String) {
      this::class.java.classLoader.getResourceAsStream(filePath)?.use { inputStream ->
        Files.copy(inputStream, root.resolve(filePath))
      } ?: throw IllegalStateException("Unable to find source: '$filePath'")
    }

    fun readText(path: String): String {
      return root
        .resolve(path)
        .toUri()
        .toURL()
        .readText()
    }
  }
}
