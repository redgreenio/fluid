package io.redgreen.fluid.dsl

import io.redgreen.fluid.commands.Command
import io.redgreen.fluid.commands.DirectoryCommand
import io.redgreen.fluid.commands.FileCopyCommand
import io.redgreen.fluid.commands.TemplateCommand
import io.redgreen.fluid.dsl.Resource.Companion.SAME_AS_DESTINATION

class Scaffold(
  private val block: Scaffold.() -> Unit
) {
  private val commands = mutableListOf<Command>()
  private var currentPath = ""

  fun directory(
    path: String,
    block: Directory.() -> Unit = { /* empty */ }
  ) {
    val previousPath = currentPath
    currentPath += if (currentPath.isEmpty()) path else "/$path"
    commands.add(DirectoryCommand(currentPath))
    block(Directory(currentPath, commands))
    currentPath = previousPath
  }

  fun fileCopy(fileName: String, resource: Resource = SAME_AS_DESTINATION) {
    commands.add(FileCopyCommand(fileName, resource))
  }

  fun <T> template(fileName: String, model: T) {
    commands.add(TemplateCommand(fileName, model))
  }

  internal fun prepare(): List<Command> {
    block()
    return commands.toList()
  }
}
