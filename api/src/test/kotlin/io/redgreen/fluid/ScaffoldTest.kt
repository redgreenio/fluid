package io.redgreen.fluid

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.assist.CommandCapturingSnapshot
import io.redgreen.fluid.assist.CommandCapturingSnapshotFactory
import io.redgreen.fluid.dsl.scaffold
import org.junit.jupiter.api.Test

class ScaffoldTest {
  @Test
  fun `it should ensure prepare call is idempotent`() {
    // given
    val scaffold = scaffold {
      dir("src")
    }

    // when
    val commands = with(scaffold) {
      transformDslToCommands()
      transformDslToCommands()
      transformDslToCommands()
      transformDslToCommands()
    }

    // then
    assertThat(commands)
      .containsExactly(DirectoryCommand("src"))
  }

  @Test
  fun `it should dispatch all commands on the snapshot`() {
    // given
    val scaffold = scaffold {
      dir("src")
      file("README.md")
      template("build.gradle", "io.redgreen")
    }
    val commands = scaffold.transformDslToCommands()

    // when
    val snapshot = scaffold
      .buildSnapshot(CommandCapturingSnapshotFactory(), Unit) as CommandCapturingSnapshot

    // then
    assertThat(snapshot.capturedCommands)
      .isEqualTo(commands)
  }
}
