package io.redgreen.fluid.assist

import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.Command
import io.redgreen.fluid.dsl.Scaffold

class ScaffoldSubject(
  metadata: FailureMetadata,
  private val actual: Scaffold
) : Subject(metadata, actual) {
  companion object {
    private val scaffoldSubjects = Factory(::ScaffoldSubject)

    @JvmStatic
    fun assertThat(scaffold: Scaffold): ScaffoldSubject =
      assertAbout(scaffoldSubjects).that(scaffold)
  }

  fun hasCommands(
    command: Command,
    vararg commands: Command
  ) {
    val actualCommands = actual.transformDslToCommands()
    assertThat(actualCommands)
      .containsExactly(command, *commands)
      .inOrder()
  }
}
