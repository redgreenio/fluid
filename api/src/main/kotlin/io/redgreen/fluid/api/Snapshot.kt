package io.redgreen.fluid.api

interface Snapshot {
  fun execute(command: DirectoryCommand)
  fun execute(command: FileCommand)
  fun <T : Any> execute(command: TemplateCommand<T>)
}
