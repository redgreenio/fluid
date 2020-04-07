package io.redgreen.fluid

import io.redgreen.fluid.Resource.Companion.SAME_AS_DESTINATION

class Directory(
  private val currentPath: String,
  private val commands: MutableList<Command>
) {
  fun file(fileName: String, resource: Resource = SAME_AS_DESTINATION) {
    commands.add(FileCommand("$currentPath/$fileName", resource))
  }

  fun <T> template(fileName: String, model: T, resource: Resource = SAME_AS_DESTINATION) {
    commands.add(TemplateCommand("$currentPath/$fileName", model, resource))
  }
}
