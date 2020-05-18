package io.redgreen.fluid.api

import io.redgreen.fluid.dsl.Permission.READ_WRITE
import io.redgreen.fluid.dsl.Source
import io.redgreen.fluid.dsl.Source.Companion.MIRROR_DESTINATION

/**
 * Commands capture information expressed in the DSL and pass it downstream. In this case,
 * it is the [Snapshot] implementation that consumes these commands.
 */
sealed class Command

/**
 * The directory command is responsible for creating new directories.
 *
 * @property directory name or path of the directory to create.
 */
data class DirectoryCommand(
  val directory: String
) : Command()

/**
 * The file command contains information for copying files from the resources directory.
 *
 * @property file name or path of the file in the resources directory.
 * @property source an explicit source file path, if required.
 * @property permissions the file's permissions. Defaults to [READ_WRITE].
 */
data class FileCommand(
  val file: String,
  val source: Source = MIRROR_DESTINATION,
  val permissions: Int = READ_WRITE
) : Command()

/**
 * The template command contains information for copying the rendered templates as files from
 * the resources directory.
 *
 * @param M the type of the model used for rendering the template.
 * @property template name or path of the template in the resources directory.
 * @property model the model object used to render the template.
 * @property source an explicit source template path, if required.
 * @property permissions the template's permissions. Defaults to [READ_WRITE].
 */
data class TemplateCommand<M : Any>(
  val template: String,
  val model: M,
  val source: Source = MIRROR_DESTINATION,
  val permissions: Int = READ_WRITE
) : Command()

/**
 * The copy directory command contains information for copying directories from the resources directory.
 *
 * @property directory name or path of the directory in the resources directory.
 * @property source an explicit source directory path, if required.
 */
data class CopyDirectoryCommand(
  val directory: String,
  val source: Source = MIRROR_DESTINATION
) : Command()
