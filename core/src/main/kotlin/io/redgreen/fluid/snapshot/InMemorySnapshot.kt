package io.redgreen.fluid.snapshot

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Resource
import io.redgreen.fluid.template.FreemarkerTemplateEngine
import io.redgreen.fluid.template.TemplateEngine
import java.io.ByteArrayInputStream
import java.nio.file.FileSystem
import java.nio.file.Files

class InMemorySnapshot private constructor(
  fileSystem: FileSystem,
  private val templateEngine: TemplateEngine
) : Snapshot {
  private val snapshotRoot = fileSystem.getPath("")

  constructor() : this(
    Jimfs.newFileSystem(Configuration.unix()),
    FreemarkerTemplateEngine()
  )

  override fun execute(command: DirectoryCommand) {
    createDirectory(command.path)
  }

  override fun execute(command: FileCommand) {
    copyFile(command.destinationPath, command.resource)
  }

  override fun <T : Any> execute(command: TemplateCommand<T>) {
    copyTemplate(command.fileName, command.model, command.resource)
  }

  fun directoryExists(path: String): Boolean {
    val resolvedPath = snapshotRoot.resolve(path)
    return Files.exists(resolvedPath) && Files.isDirectory(resolvedPath)
  }

  fun fileExists(path: String): Boolean {
    val resolvedPath = snapshotRoot.resolve(path)
    return Files.exists(resolvedPath) && !Files.isDirectory(resolvedPath)
  }

  fun readText(path: String): String {
    return snapshotRoot
      .resolve(path)
      .toUri()
      .toURL()
      .readText()
  }

  private fun createDirectory(path: String) {
    Files.createDirectories(snapshotRoot.resolve(path))
  }

  private fun copyFile(destination: String, resource: Resource) {
    val source = if (resource.isSameAsDestination()) destination else resource.filePath

    this::class.java.classLoader.getResourceAsStream(source)?.use { inputStream ->
      Files.copy(inputStream, snapshotRoot.resolve(destination))
    } ?: throw IllegalStateException("Unable to find source: '$source'")
  }

  private fun <T : Any> copyTemplate(
    destination: String,
    model: T,
    resource: Resource
  ) {
    val templatePath = if (resource.isSameAsDestination()) destination else resource.filePath
    val processedTemplate = templateEngine.processTemplate(templatePath, model)
    ByteArrayInputStream(processedTemplate.toByteArray()).use { inputStream ->
      Files.copy(inputStream, this.snapshotRoot.resolve(destination))
    }
  }
}
