package io.redgreen.fluid.commands

data class TemplateCommand<T>(
  val fileName: String,
  val params: T
) : Command
