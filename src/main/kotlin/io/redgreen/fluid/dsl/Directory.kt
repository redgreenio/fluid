package io.redgreen.fluid.dsl

import io.redgreen.fluid.commands.Command
import io.redgreen.fluid.commands.FileCopyCommand

class Directory(
  private val currentPath: String,
  private val commands: MutableList<Command>
) {
  fun fileCopy(fileName: String) {
    commands.add(FileCopyCommand("$currentPath/$fileName"))
  }
}
