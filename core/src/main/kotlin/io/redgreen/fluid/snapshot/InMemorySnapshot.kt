package io.redgreen.fluid.snapshot

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.google.common.jimfs.PathType
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.DirectoryEntry
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.api.FileSystemEntry
import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Resource
import io.redgreen.fluid.template.FreemarkerTemplateEngine
import io.redgreen.fluid.template.TemplateEngine
import java.io.ByteArrayInputStream
import java.nio.file.FileSystem
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

class InMemorySnapshot private constructor(
  generatorClass: Class<out Generator>,
  fileSystem: FileSystem,
  private val templateEngine: TemplateEngine
) : Snapshot(generatorClass) {
  companion object {
    private const val ROOT = "/"

    private val UNIX_CONFIGURATION = Configuration
      .builder(PathType.unix())
      .setRoots(ROOT)
      .setWorkingDirectory(ROOT)
      .setAttributeViews("basic")
      .build()

    fun forGenerator(generatorClass: Class<out Generator>): InMemorySnapshot {
      return InMemorySnapshot(generatorClass)
    }
  }

  private val snapshotRoot = fileSystem.getPath(ROOT)

  private constructor(generatorClass: Class<out Generator>) : this(
    generatorClass,
    Jimfs.newFileSystem(UNIX_CONFIGURATION),
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

  override fun getEntries(): List<FileSystemEntry> {
    val allPaths = Files
      .walk(snapshotRoot, FileVisitOption.FOLLOW_LINKS)
      .filter { it != snapshotRoot }

    return allPaths
      .map(::createFileSystemEntry)
      .toList()
      .normalize()
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

  fun readBytes(path: String): ByteArray {
    return snapshotRoot
      .resolve(path)
      .toUri()
      .toURL()
      .readBytes()
  }

  private fun createDirectory(path: String) {
    Files.createDirectories(snapshotRoot.resolve(path))
  }

  private fun copyFile(destination: String, resource: Resource) {
    val source = if (resource.isSameAsDestination()) destination else resource.filePath

    classLoaderClass.classLoader.getResourceAsStream(source)?.use { inputStream ->
      Files.copy(inputStream, snapshotRoot.resolve(destination))
    } ?: throw IllegalStateException("Unable to find source file: '$source'")
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

  private fun createFileSystemEntry(
    path: Path
  ): FileSystemEntry {
    val pathWithoutLeadingSlash = path.toString().substring(1)
    return if (Files.isDirectory(path)) {
      DirectoryEntry(pathWithoutLeadingSlash)
    } else {
      FileEntry(pathWithoutLeadingSlash)
    }
  }

  private fun List<FileSystemEntry>.normalize(): List<FileSystemEntry> {
    val fileEntries = this.filterIsInstance<FileEntry>()
    val directoryEntries = normalizeDirectoryEntries(this.filterIsInstance<DirectoryEntry>())
    return fileEntries + directoryEntries
  }

  private fun normalizeDirectoryEntries(directoryEntries: List<DirectoryEntry>): List<DirectoryEntry> {
    val pathsTree = Node.buildPathsTree(directoryEntries.map { it.path })
    return pathsTree.leaves().map(::DirectoryEntry)
  }
}
