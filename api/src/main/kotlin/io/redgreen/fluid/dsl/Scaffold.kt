package io.redgreen.fluid.dsl

import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.SnapshotFactory
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Source.Companion.MIRROR_DESTINATION

class Scaffold(
  private val block: Scaffold.() -> Unit
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
    commands.add(FileCommand(name, source))
  }

  fun <M : Any> template(
    name: String,
    model: M,
    source: Source = MIRROR_DESTINATION
  ) {
    commands.add(TemplateCommand(name, model, source))
  }

  fun <S : Any> buildSnapshot(
    snapshotFactory: SnapshotFactory<S>,
    snapshotParams: S
  ): Snapshot {
    val snapshot = snapshotFactory.newInstance(snapshotParams)
    transformDslToCommands().onEach { command -> dispatchCommandsToSnapshot(snapshot, command) }
    return snapshot
  }

  internal fun transformDslToCommands(): List<Command> {
    commands.clear()
    block()
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
