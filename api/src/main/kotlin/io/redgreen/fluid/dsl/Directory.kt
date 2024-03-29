package io.redgreen.fluid.dsl

import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.CopyDirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Permission.READ_WRITE
import io.redgreen.fluid.dsl.Scaffold.Companion.UNIX_PATH_SEPARATOR
import io.redgreen.fluid.dsl.Source.Companion.MIRROR_DESTINATION

/**
 * The [Directory] class enables nesting of files, templates and directories in the DSL.
 */
class Directory internal constructor(
  private val currentPath: String,
  private val commands: MutableList<Command>
) {
  /**
   * Copy a file.
   *
   * @param name name of the file.
   * @param source an explicit source, if required.
   */
  fun file(
    name: String,
    source: Source = MIRROR_DESTINATION
  ) {
    file(name, source, READ_WRITE)
  }

  /**
   * Copy a rendered template.
   *
   * @param M the template model.
   * @param name name of the template.
   * @param model the model object for rendering the template.
   * @param source an explicit source, if required.
   */
  fun <M : Any> template(
    name: String,
    model: M,
    source: Source = MIRROR_DESTINATION
  ) {
    template(name, model, source, READ_WRITE)
  }

  /**
   * Copy a file.
   *
   * @param name name of the file.
   * @param permissions permissions for the file, from the [Permission] class.
   */
  fun file(
    name: String,
    permissions: Int
  ) {
    file(name, MIRROR_DESTINATION, permissions)
  }

  /**
   * Copy a file.
   *
   * @param name name of the file.
   * @param source an explicit source.
   * @param permissions permissions for the file, from the [Permission] class.
   */
  fun file(
    name: String,
    source: Source,
    permissions: Int
  ) {
    commands.add(FileCommand("$currentPath$UNIX_PATH_SEPARATOR$name", source, permissions))
  }

  /**
   * Copy a rendered template.
   *
   * @param M the template model.
   * @param name name of the template.
   * @param model the model object for rendering the template.
   * @param permissions permissions for the file, from the [Permission] class.
   */
  fun <M : Any> template(
    name: String,
    model: M,
    permissions: Int
  ) {
    template(name, model, MIRROR_DESTINATION, permissions)
  }

  /**
   * Copy a rendered template.
   *
   * @param M the template model.
   * @param name name of the template.
   * @param model the model object for rendering the template.
   * @param source an explicit source.
   * @param permissions permissions for the file, from the [Permission] class.
   */
  fun <M : Any> template(
    name: String,
    model: M,
    source: Source,
    permissions: Int
  ) {
    commands.add(TemplateCommand("$currentPath$UNIX_PATH_SEPARATOR$name", model, source, permissions))
  }

  /**
   * Copy a directory.
   *
   * @param path the directory path.
   * @param source an explicit source, if required.
   */
  fun copyDir(
    path: String,
    source: Source = MIRROR_DESTINATION
  ) {
    commands.add(CopyDirectoryCommand("$currentPath$UNIX_PATH_SEPARATOR$path", source))
  }
}
