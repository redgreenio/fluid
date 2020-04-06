package io.redgreen.fluid.commands

import io.redgreen.fluid.dsl.Resource
import io.redgreen.fluid.dsl.Resource.Companion.SAME_AS_DESTINATION

sealed class Command

data class DirectoryCommand(
  val path: String
) : Command()

data class FileCopyCommand(
  val destinationPath: String,
  val resource: Resource = SAME_AS_DESTINATION
) : Command()

data class TemplateCommand<T>(
  val fileName: String,
  val model: T
) : Command()
