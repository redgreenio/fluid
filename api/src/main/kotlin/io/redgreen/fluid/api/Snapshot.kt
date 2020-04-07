package io.redgreen.fluid.api

import io.redgreen.fluid.DirectoryCommand
import io.redgreen.fluid.FileCommand
import io.redgreen.fluid.TemplateCommand

interface Snapshot {
  fun execute(command: DirectoryCommand)
  fun execute(command: FileCommand)
  fun <T> execute(command: TemplateCommand<T>)
}
