package io.redgreen.fluid.api

abstract class Snapshot(
  protected val classLoaderClass: Class<*>
) {
  abstract fun execute(command: DirectoryCommand)
  abstract fun execute(command: FileCommand)
  abstract fun <T : Any> execute(command: TemplateCommand<T>)
  abstract fun getEntries(): List<FileSystemEntry>
}
