package io.redgreen.fluid.api

import io.redgreen.fluid.dsl.Permission.READ_WRITE
import io.redgreen.fluid.dsl.Source
import io.redgreen.fluid.dsl.Source.Companion.MIRROR_DESTINATION

sealed class Command

data class DirectoryCommand(
  val directory: String
) : Command()

data class FileCommand(
  val file: String,
  val source: Source = MIRROR_DESTINATION,
  val permissions: Int = READ_WRITE
) : Command()

data class TemplateCommand<T : Any>(
  val template: String,
  val model: T,
  val source: Source = MIRROR_DESTINATION
) : Command()
