package io.redgreen.fluid.dsl

import io.redgreen.fluid.commands.Command
import io.redgreen.fluid.commands.FileCopyCommand
import io.redgreen.fluid.commands.TemplateCommand

class Directory(
  private val currentPath: String,
  private val commands: MutableList<Command>
) {
  fun fileCopy(fileName: String) {
    commands.add(FileCopyCommand("$currentPath/$fileName"))
  }

  fun <T> template(fileName: String, params: T) {
    commands.add(TemplateCommand("$currentPath/$fileName", params))
  }
}
