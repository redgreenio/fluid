package io.redgreen.fluid.api

import io.redgreen.fluid.dsl.Scaffold

/**
 * Generators are the lifeblood of Fluid. The [Generator] interface enables developers to write
 * custom generators that can fulfill their needs.
 *
 * @param C configuration used by the generator. Can be [Unit] if the generator does
 *          not require external configuration.
 */
interface Generator<C : Any> {
  /**
   * Builds the required configuration object for the generator. This is usually done by getting
   * inputs from the user.
   *
   * @return the configuration object.
   */
  fun configure(): C

  /**
   * Houses the [Scaffold] object and thereby the DSL used to generate desired files and directories
   * when the generator is run. This is the meat of the [Generator].
   *
   * @return the [Scaffold] object that can generate the desired files and directories.
   */
  fun scaffold(): Scaffold<C>
}
