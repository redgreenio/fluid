package io.redgreen.fluid

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.assist.CommandCapturingSnapshot
import io.redgreen.fluid.assist.CommandCapturingSnapshotFactory
import io.redgreen.fluid.dsl.scaffold
import org.junit.jupiter.api.Test

class ScaffoldTest {
  @Test
  fun `it should ensure prepare call is idempotent`() {
    // given
    val scaffold = scaffold<Unit> {
      dir("src")
    }

    // when
    val commands = with(scaffold) {
      transformDslToCommands(Unit)
      transformDslToCommands(Unit)
      transformDslToCommands(Unit)
      transformDslToCommands(Unit)
    }

    // then
    assertThat(commands)
      .containsExactly(DirectoryCommand("src"))
  }

  @Test
  fun `it should dispatch all available commands on the snapshot`() {
    // given
    val scaffold = scaffold<Unit> {
      dir("src")
      file("README.md")
      template("build.gradle", "io.redgreen")
      copyDir("gradle")
    }
    val commands = scaffold.transformDslToCommands(Unit)

    // when
    val snapshot = scaffold
      .buildSnapshot(CommandCapturingSnapshotFactory(), Unit, Unit) as CommandCapturingSnapshot

    // then
    assertThat(snapshot.capturedCommands)
      .isEqualTo(commands)

    val commandClasses = snapshot.capturedCommands.map { it::class.java }
    val availableCommandClasses = Command::class.sealedSubclasses.map { it.java }
    assertThat(commandClasses)
      .isEqualTo(availableCommandClasses)
  }
}
