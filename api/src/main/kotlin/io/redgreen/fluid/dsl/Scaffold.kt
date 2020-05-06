package io.redgreen.fluid.dsl

import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.api.SnapshotFactory
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Resource.Companion.SAME_AS_DESTINATION

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
    path: String,
    block: Directory.() -> Unit = { /* empty */ }
  ) {
    val previousPath = currentPath
    currentPath += if (currentPath.isEmpty()) path else "$UNIX_PATH_SEPARATOR$path"
    commands.add(DirectoryCommand(currentPath))
    block(Directory(currentPath, commands))
    currentPath = previousPath
  }

  fun file(fileName: String, resource: Resource = SAME_AS_DESTINATION) {
    commands.add(FileCommand(fileName, resource))
  }

  fun <T : Any> template(fileName: String, model: T, resource: Resource = SAME_AS_DESTINATION) {
    commands.add(TemplateCommand(fileName, model, resource))
  }

  fun <T : Any> buildSnapshot(factory: SnapshotFactory<T>, param: T): Snapshot {
    val snapshot = factory.newInstance(param)
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
