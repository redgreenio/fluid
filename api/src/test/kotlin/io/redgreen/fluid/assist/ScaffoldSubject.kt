package io.redgreen.fluid.assist

import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.Command
import io.redgreen.fluid.dsl.Scaffold

class ScaffoldSubject<C : Any>(
  metadata: FailureMetadata,
  private val actual: Scaffold<C>
) : Subject(metadata, actual) {
  companion object {
    private val scaffoldSubjects = Factory<ScaffoldSubject<*>, Scaffold<*>> { metadata, actual ->
      ScaffoldSubject(metadata, actual)
    }

    @JvmStatic
    fun assertThat(scaffold: Scaffold<*>): ScaffoldSubject<*> =
      assertAbout(scaffoldSubjects).that(scaffold)
  }

  fun produces(
    command: Command,
    vararg commands: Command
  ) {
    val actualCommands = actual.transformDslToCommands(Unit)
    assertThat(actualCommands)
      .containsExactly(command, *commands)
      .inOrder()
  }
}
