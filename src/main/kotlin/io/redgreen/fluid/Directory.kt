package io.redgreen.fluid

class Directory(
  private val currentPath: String,
  private val commands: MutableList<Command>
) {
  fun fileCopy(fileName: String) {
    commands.add(FileCopyCommand("$currentPath/$fileName"))
  }
}
