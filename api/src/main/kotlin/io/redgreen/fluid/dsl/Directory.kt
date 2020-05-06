package io.redgreen.fluid.dsl

import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Source.Companion.MIRROR_DESTINATION

class Directory(
  private val currentPath: String,
  private val commands: MutableList<Command>
) {
  fun file(
    name: String,
    source: Source = MIRROR_DESTINATION
  ) {
    commands.add(FileCommand("$currentPath/$name", source))
  }

  fun <M : Any> template(
    name: String,
    model: M,
    source: Source = MIRROR_DESTINATION
  ) {
    commands.add(TemplateCommand("$currentPath/$name", model, source))
  }
}
