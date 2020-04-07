package io.redgreen.fluid

import io.redgreen.fluid.Resource.Companion.SAME_AS_DESTINATION

sealed class Command

data class DirectoryCommand(
  val path: String
) : Command()

data class FileCommand(
  val destinationPath: String,
  val resource: Resource = SAME_AS_DESTINATION
) : Command()

data class TemplateCommand<T>(
  val fileName: String,
  val model: T,
  val resource: Resource = SAME_AS_DESTINATION
) : Command()
