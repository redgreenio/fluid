package io.redgreen.fluid.dsl

import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.CopyDirectoryCommand
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.SnapshotFactory
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Permission.READ_WRITE
import io.redgreen.fluid.dsl.Source.Companion.MIRROR_DESTINATION

class Scaffold<in C : Any>(
  private val block: Scaffold<C>.(C) -> Unit
) {
  companion object {
    private const val ROOT = ""
    private const val UNIX_PATH_SEPARATOR = "/"
  }

  private var currentPath = ROOT
  private val commands = mutableListOf<Command>()

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

  fun file(
    name: String,
    source: Source = MIRROR_DESTINATION
  ) {
    file(name, source, READ_WRITE)
  }

  fun <M : Any> template(
    name: String,
    model: M,
    source: Source = MIRROR_DESTINATION
  ) {
    template(name, model, source, READ_WRITE)
  }

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

  fun file(
    name: String,
    permissions: Int
  ) {
    file(name, MIRROR_DESTINATION, permissions)
  }

  fun file(
    name: String,
    source: Source,
    permissions: Int
  ) {
    commands.add(FileCommand(name, source, permissions))
  }

  fun <M : Any> template(
    name: String,
    model: M,
    permissions: Int
  ) {
    template(name, model, MIRROR_DESTINATION, permissions)
  }

  fun <M : Any> template(
    name: String,
    model: M,
    source: Source,
    permissions: Int
  ) {
    commands.add(TemplateCommand(name, model, source, permissions))
  }

  fun copyDir(
    name: String,
    source: Source = MIRROR_DESTINATION
  ) {
    commands.add(CopyDirectoryCommand(name, source))
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
    }
  }
}
