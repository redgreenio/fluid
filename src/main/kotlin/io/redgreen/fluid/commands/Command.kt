package io.redgreen.fluid.commands

sealed class Command

data class DirectoryCommand(
  val path: String
) : Command()

data class FileCopyCommand(
  val fileName: String
) : Command()

data class TemplateCommand<T>(
  val fileName: String,
  val params: T
) : Command()
