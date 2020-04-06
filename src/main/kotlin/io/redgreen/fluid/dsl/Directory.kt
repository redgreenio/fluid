package io.redgreen.fluid.dsl

import io.redgreen.fluid.commands.Command
import io.redgreen.fluid.commands.FileCopyCommand
import io.redgreen.fluid.commands.TemplateCommand
import io.redgreen.fluid.dsl.Resource.Companion.SAME_AS_DESTINATION

class Directory(
  private val currentPath: String,
  private val commands: MutableList<Command>
) {
  fun fileCopy(fileName: String, resource: Resource = SAME_AS_DESTINATION) {
    commands.add(FileCopyCommand("$currentPath/$fileName", resource))
  }

  fun <T> template(fileName: String, params: T) {
    commands.add(TemplateCommand("$currentPath/$fileName", params))
  }
}
