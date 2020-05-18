package io.redgreen.fluid.dsl

import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.CopyDirectoryCommand
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.SnapshotFactory
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Permission.READ_WRITE
import io.redgreen.fluid.dsl.Source.Companion.MIRROR_DESTINATION

/**
 * Scaffold enables developers to express intent for the file and directory structure that fits their use case.
 * An instance of the object can be created using the [scaffold] function.
 */
class Scaffold<in C : Any> internal constructor(
  private val block: Scaffold<C>.(C) -> Unit
) {
  companion object {
    internal const val UNIX_PATH_SEPARATOR = "/"
    private const val ROOT = ""
  }

  private var currentPath = ROOT
  private val commands = mutableListOf<Command>()

  /**
   * Create a directory.
   *
   * @param segment directory name or path segment.
   * @param block lambda block used to nest files, templates, and other directories.
   */
  fun dir(
    segment: String,
    block: Directory.() -> Unit = { /* empty */ }
  ) {
    val previousPath = currentPath
    currentPath += if (currentPath.isEmpty()) segment else "$UNIX_PATH_SEPARATOR$segment"
    commands.add(DirectoryCommand(currentPath))
    block(Directory(currentPath, commands))
    currentPath = previousPath
  }

  /**
   * Copy a file.
   *
   * @param name name of the file.
   * @param source an explicit source, if required.
   */
  fun file(
    name: String,
    source: Source = MIRROR_DESTINATION
  ) {
    file(name, source, READ_WRITE)
  }

  /**
   * Copy a rendered template.
   *
   * @param M the template model.
   * @param name name of the template.
   * @param model the model object for rendering the template.
   * @param source an explicit source, if required.
   */
  fun <M : Any> template(
    name: String,
    model: M,
    source: Source = MIRROR_DESTINATION
  ) {
    template(name, model, source, READ_WRITE)
  }

  /**
   * Builds a [Snapshot] from the [Scaffold].
   *
   * @param S information required to construct a [Snapshot] object.
   * @param snapshotFactory a [SnapshotFactory] instance.
   * @param dslConfig configuration object received from [Generator.configure].
   */
  fun <S : Any> buildSnapshot(
    snapshotFactory: SnapshotFactory<S>,
    snapshotParams: S,
    dslConfig: Any
  ): Snapshot {
    val snapshot = snapshotFactory.newInstance(snapshotParams)
    transformDslToCommands(dslConfig as C).onEach { command ->
      dispatchCommandsToSnapshot(snapshot, command)
    }
    return snapshot
  }

  /**
   * Copy a file.
   *
   * @param name name of the file.
   * @param permissions permissions for the file, from the [Permission] class.
   */
  fun file(
    name: String,
    permissions: Int
  ) {
    file(name, MIRROR_DESTINATION, permissions)
  }

  /**
   * Copy a file.
   *
   * @param name name of the file.
   * @param source an explicit source.
   * @param permissions permissions for the file, from the [Permission] class.
   */
  fun file(
    name: String,
    source: Source,
    permissions: Int
  ) {
    commands.add(FileCommand(name, source, permissions))
  }

  /**
   * Copy a rendered template.
   *
   * @param M the template model.
   * @param name name of the template.
   * @param model the model object for rendering the template.
   * @param permissions permissions for the file, from the [Permission] class.
   */
  fun <M : Any> template(
    name: String,
    model: M,
    permissions: Int
  ) {
    template(name, model, MIRROR_DESTINATION, permissions)
  }

  /**
   * Copy a rendered template.
   *
   * @param M the template model.
   * @param name name of the template.
   * @param model the model object for rendering the template.
   * @param source an explicit source.
   * @param permissions permissions for the file, from the [Permission] class.
   */
  fun <M : Any> template(
    name: String,
    model: M,
    source: Source,
    permissions: Int
  ) {
    commands.add(TemplateCommand(name, model, source, permissions))
  }

  /**
   * Copy a directory.
   *
   * @param path the directory path.
   * @param source an explicit source, if required.
   */
  fun copyDir(
    path: String,
    source: Source = MIRROR_DESTINATION
  ) {
    commands.add(CopyDirectoryCommand(path, source))
  }

  internal fun transformDslToCommands(
    dslConfig: Any
  ): List<Command> {
    commands.clear()
    block(dslConfig as C)
    commands.ifEmpty {
      val message = "The scaffold is empty. You can make it useful by " +
        "creating directories, copying files, or templates."
      throw IllegalStateException(message)
    }

    return commands.toList()
  }

  private fun dispatchCommandsToSnapshot(
    snapshot: Snapshot,
    command: Command
  ) {
    when (command) {
      is DirectoryCommand -> snapshot.execute(command)
      is FileCommand -> snapshot.execute(command)
      is TemplateCommand<*> -> snapshot.execute(command)
      is CopyDirectoryCommand -> snapshot.execute(command)
    }
  }
}
