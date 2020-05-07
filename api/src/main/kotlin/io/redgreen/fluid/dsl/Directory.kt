package io.redgreen.fluid.dsl

import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Permission.READ_WRITE
import io.redgreen.fluid.dsl.Source.Companion.MIRROR_DESTINATION

class Directory(
  private val currentPath: String,
  private val commands: MutableList<Command>
) {
  fun file(
    name: String,
    source: Source = MIRROR_DESTINATION
  ) {
    file(name, source, READ_WRITE)
  }

  fun <M : Any> template(
    name: String,
    model: M,
    source: Source = MIRROR_DESTINATION
  ) {
    commands.add(TemplateCommand("$currentPath/$name", model, source))
  }

  fun file(
    name: String,
    permissions: Int
  ) {
    file(name, MIRROR_DESTINATION, permissions)
  }

  fun file(
    name: String,
    source: Source,
    permissions: Int
  ) {
    commands.add(FileCommand("$currentPath/$name", source, permissions))
  }
}
