package io.redgreen.fluid

import io.redgreen.fluid.Resource.Companion.SAME_AS_DESTINATION

fun scaffold(block: Scaffold.() -> Unit): Scaffold =
  Scaffold(block)

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

  fun <T> template(fileName: String, model: T, resource: Resource = SAME_AS_DESTINATION) {
    commands.add(TemplateCommand(fileName, model, resource))
  }

  fun prepare(): List<Command> {
    block()
    return commands.toList()
  }
}
