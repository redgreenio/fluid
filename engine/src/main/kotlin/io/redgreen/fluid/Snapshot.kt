package io.redgreen.fluid

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration.VERSION_2_3_30
import java.io.ByteArrayInputStream
import java.io.StringWriter
import java.nio.file.FileSystem
import java.nio.file.Files
import freemarker.template.Configuration as FreemarkerConfiguration

sealed class Snapshot {
  object Empty : Snapshot()

  class InMemory private constructor(
    fileSystem: FileSystem
  ) : Snapshot() {
    private val root = fileSystem.getPath("")

    constructor() : this(Jimfs.newFileSystem(Configuration.unix()))

    fun directoryExists(path: String): Boolean {
      val resolvedPath = root.resolve(path)
      return Files.exists(resolvedPath) && Files.isDirectory(resolvedPath)
    }

    fun fileExists(path: String): Boolean {
      val resolvedPath = root.resolve(path)
      return Files.exists(resolvedPath) && !Files.isDirectory(resolvedPath)
    }

    fun readText(path: String): String {
      return root
        .resolve(path)
        .toUri()
        .toURL()
        .readText()
    }

    internal fun execute(command: Command) {
      @Suppress("UNUSED_VARIABLE") // Because, we need exhaustive handling of all command types
      val x = when (command) {
        is DirectoryCommand -> createDirectory(command.path)
        is FileCopyCommand -> copyFile(command.destinationPath, command.resource)
        is TemplateCommand<*> -> copyTemplate(command.fileName, command.model, command.resource)
      }
    }

    // TODO(rj) 6-Apr-20 Return a result sealed class with success and failure
    private fun createDirectory(path: String) {
      Files.createDirectories(root.resolve(path))
    }

    private fun copyFile(destination: String, resource: Resource) {
      val source = if (resource.isSameAsDestination()) destination else resource.filePath

      this::class.java.classLoader.getResourceAsStream(source)?.use { inputStream ->
        Files.copy(inputStream, root.resolve(destination))
      } ?: throw IllegalStateException("Unable to find source: '$source'")
    }

    private fun <T> copyTemplate(destination: String, model: T, resource: Resource) {
      val source = if (resource.isSameAsDestination()) destination else resource.filePath

      // TODO Cache these, they are expensive to create
      val configuration = FreemarkerConfiguration(VERSION_2_3_30).apply {
        defaultEncoding = "UTF-8"
        templateLoader = ClassTemplateLoader(Fluid::class.java.classLoader, "")
      }

      val root = mapOf(
        "model" to model
      )

      val template = configuration.getTemplate(source)
      val writer = StringWriter()
      template.process(root, writer)

      val processedTemplate = writer.toString()

      ByteArrayInputStream(processedTemplate.toByteArray()).use { inputStream ->
        Files.copy(inputStream, this.root.resolve(destination))
      }
    }
  }
}
