package io.redgreen.fluid.snapshot

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import com.google.common.jimfs.PathType
import io.redgreen.fluid.api.CopyDirectoryCommand
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.DirectoryEntry
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.api.FileSystemEntry
import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Permission.EXECUTE
import io.redgreen.fluid.dsl.Permission.READ_WRITE
import io.redgreen.fluid.dsl.Source
import io.redgreen.fluid.template.FreemarkerTemplateEngine
import io.redgreen.fluid.template.TemplateEngine
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.nio.file.FileSystem
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE
import java.util.Optional
import kotlin.streams.toList

class InMemorySnapshot private constructor(
  generatorClass: Class<out Generator<*>>,
  fileSystem: FileSystem,
  private val templateEngine: TemplateEngine
) : Snapshot {
  companion object {
    private const val ROOT = "/"

    private val UNIX_CONFIGURATION = Configuration
      .builder(PathType.unix())
      .setRoots(ROOT)
      .setWorkingDirectory(ROOT)
      .setAttributeViews("posix")
      .build()

    internal fun forGenerator(generatorClass: Class<out Generator<*>>): Snapshot =
      InMemorySnapshot(generatorClass)
  }

  private val classLoader = generatorClass.classLoader
  private val snapshotRoot = fileSystem.getPath(ROOT)

  private constructor(generatorClass: Class<out Generator<*>>) : this(
    generatorClass,
    Jimfs.newFileSystem(UNIX_CONFIGURATION),
    FreemarkerTemplateEngine(generatorClass)
  )

  override fun execute(command: DirectoryCommand) {
    createDirectory(command.directory)
  }

  override fun execute(command: FileCommand) {
    with(command) { copyFile(file, source, permissions) }
  }

  override fun <T : Any> execute(command: TemplateCommand<T>) {
    copyTemplate(command.template, command.model, command.source, command.permissions)
  }

  override fun execute(command: CopyDirectoryCommand) {
    val sourceDirectory = classLoader.getResource(command.directory)?.path
      ?: throw IllegalStateException("Unable to find '${command.directory}' in the generator's 'resources' directory.")
    copyDirectory(command.directory, sourceDirectory)
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
    directory: String,
    sourceDirectoryPath: String
  ) {
    val sourceDirectory = File(sourceDirectoryPath)
    val filesInSourceDirectory = mutableListOf<File>()
    findFilesInDirectory(sourceDirectory, filesInSourceDirectory)

    if (filesInSourceDirectory.isEmpty()) {
      createDirectory(directory)
    } else {
      filesInSourceDirectory.onEach { sourceFile ->
        val sourceFilePath = sourceFile.path
        val relativeFilePath = sourceFilePath.substring(sourceFilePath.indexOf(directory), sourceFilePath.length)
        copyFile(relativeFilePath, Source(relativeFilePath), READ_WRITE)
      }
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

  private fun copyFile(
    file: String,
    source: Source,
    permissions: Int
  ) {
    val sourceFilePath = if (source.mirrorsDestination) file else source.path
    createMissingDirectoriesInPath(file)
    classLoader.getResourceAsStream(sourceFilePath)?.use { inputStream ->
      val targetFilePath = snapshotRoot.resolve(file)
      if (!Files.exists(targetFilePath)) {
        Files.copy(inputStream, targetFilePath)
        if (permissions == EXECUTE) {
          Files.setPosixFilePermissions(targetFilePath, mutableSetOf(OTHERS_EXECUTE))
        }
      }
    } ?: throw IllegalStateException("Unable to find source file: '$sourceFilePath'") // TODO: Add tests for missing files and templates
  }

  private fun <T : Any> copyTemplate(
    file: String,
    model: T,
    source: Source,
    permissions: Int
  ) {
    val sourceFilePath = if (source.mirrorsDestination) file else source.path
    val processedTemplate = templateEngine.processTemplate(sourceFilePath, model)
    createMissingDirectoriesInPath(file)
    ByteArrayInputStream(processedTemplate.toByteArray()).use { inputStream ->
      val targetFilePath = snapshotRoot.resolve(file)
      Files.copy(inputStream, targetFilePath)
      if (permissions == EXECUTE) {
        Files.setPosixFilePermissions(targetFilePath, mutableSetOf(OTHERS_EXECUTE))
      }
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
      val hasExecutePermission = Files.getPosixFilePermissions(path).contains(OTHERS_EXECUTE)
      if (hasExecutePermission) {
        FileEntry(pathWithoutLeadingSlash, EXECUTE)
      } else {
        FileEntry(pathWithoutLeadingSlash)
      }
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
