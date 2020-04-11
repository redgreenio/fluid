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
import java.io.File
import java.io.InputStream
import java.nio.file.FileSystem
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional
import kotlin.streams.toList

class InMemorySnapshot private constructor(
  generatorClass: Class<out Generator>,
  fileSystem: FileSystem,
  private val templateEngine: TemplateEngine
) : Snapshot {
  companion object {
    private const val ROOT = "/"

    private val UNIX_CONFIGURATION = Configuration
      .builder(PathType.unix())
      .setRoots(ROOT)
      .setWorkingDirectory(ROOT)
      .setAttributeViews("basic")
      .build()

    internal fun forGenerator(generatorClass: Class<out Generator>): Snapshot =
      InMemorySnapshot(generatorClass)
  }

  private val classLoader = generatorClass.classLoader
  private val snapshotRoot = fileSystem.getPath(ROOT)

  private constructor(generatorClass: Class<out Generator>) : this(
    generatorClass,
    Jimfs.newFileSystem(UNIX_CONFIGURATION),
    FreemarkerTemplateEngine(generatorClass)
  )

  override fun execute(command: DirectoryCommand) {
    val resourceDirectoryPath = classLoader.getResource(command.path)?.path
    if (resourceDirectoryPath == null) {
      createDirectory(command.path)
    } else {
      copyDirectory(command, resourceDirectoryPath)
    }
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

  override fun inputStream(path: String): Optional<InputStream> {
    val inputStream = try {
      snapshotRoot.resolve(path).toUri().toURL().openStream()
    } catch (exception: Exception) {
      exception.printStackTrace()
      null
    }
    return inputStream?.let { Optional.of(it) } ?: Optional.empty()
  }

  private fun createDirectory(path: String) {
    Files.createDirectories(snapshotRoot.resolve(path))
  }

  private fun copyDirectory(
    command: DirectoryCommand,
    resourceDirectoryPath: String
  ) {
    val sourceDirectory = File(resourceDirectoryPath)
    val filesInSourceDirectory = mutableListOf<File>()
    findFilesInDirectory(sourceDirectory, filesInSourceDirectory)

    filesInSourceDirectory.onEach { sourceFile ->
      val sourceFilePath = sourceFile.path
      val destination = sourceFilePath.substring(sourceFilePath.indexOf(command.path), sourceFilePath.length)
      copyFile(destination, Resource(destination))
    }
  }

  private fun findFilesInDirectory(
    directory: File,
    filesCollector: MutableList<File>
  ) {
    val filesFound = directory.listFiles()?.toList() ?: return

    for (file in filesFound) {
      if (file.isFile) {
        filesCollector.add(file)
      } else {
        findFilesInDirectory(file, filesCollector)
      }
    }
  }

  private fun copyFile(destination: String, resource: Resource) {
    val source = if (resource.isSameAsDestination()) destination else resource.filePath
    createMissingDirectoriesInPath(destination)
    classLoader.getResourceAsStream(source)?.use { inputStream ->
      Files.copy(inputStream, snapshotRoot.resolve(destination))
    } ?: throw IllegalStateException("Unable to find source file: '$source'") // TODO: Add tests for missing files and templates
  }

  private fun <T : Any> copyTemplate(
    destination: String,
    model: T,
    resource: Resource
  ) {
    val templatePath = if (resource.isSameAsDestination()) destination else resource.filePath
    val processedTemplate = templateEngine.processTemplate(templatePath, model)
    createMissingDirectoriesInPath(destination)
    ByteArrayInputStream(processedTemplate.toByteArray()).use { inputStream ->
      Files.copy(inputStream, this.snapshotRoot.resolve(destination))
    }
  }

  private fun createMissingDirectoriesInPath(destination: String) {
    Files.createDirectories(snapshotRoot.resolve(destination).parent)
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
