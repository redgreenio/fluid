package io.redgreen.fluid.api

import io.redgreen.fluid.dsl.Resource
import io.redgreen.fluid.dsl.Resource.Companion.SAME_AS_DESTINATION

sealed class Command

data class DirectoryCommand(
  val directory: String
) : Command()

data class FileCommand(
  val file: String,
  val resource: Resource = SAME_AS_DESTINATION
) : Command()

data class TemplateCommand<T : Any>(
  val template: String,
  val model: T,
  val resource: Resource = SAME_AS_DESTINATION
) : Command()
