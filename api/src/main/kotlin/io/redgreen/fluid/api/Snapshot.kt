package io.redgreen.fluid.api

interface Snapshot {
  fun execute(command: DirectoryCommand)
  fun execute(command: FileCommand)
  fun <T> execute(command: TemplateCommand<T>)
}
