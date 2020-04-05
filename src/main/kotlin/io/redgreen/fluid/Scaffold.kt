package io.redgreen.fluid

class Scaffold(
  private val block: Scaffold.() -> Unit
) {
  private val commands = mutableListOf<Command>()
  private var currentPath = ""

  fun directory(
    path: String,
    block: Scaffold.() -> Unit = { /* empty */ }
  ) {
    val previousPath = currentPath
    currentPath += if (currentPath.isEmpty()) path else "/$path"
    commands.add(DirectoryCommand(currentPath))
    block()
    currentPath = previousPath
  }

  fun fileCopy(fileName: String) {
    commands.add(FileCopyCommand(fileName))
  }

  internal fun prepare(): List<Command> {
    block()
    return commands.toList()
  }
}
