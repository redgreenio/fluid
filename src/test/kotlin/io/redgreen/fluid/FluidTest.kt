package io.redgreen.fluid

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.Snapshot.Empty
import io.redgreen.fluid.Snapshot.InMemory
import io.redgreen.fluid.commands.Command
import io.redgreen.fluid.commands.DirectoryCommand
import io.redgreen.fluid.truth.InMemorySubject.Companion.assertThat
import org.junit.jupiter.api.Test

class FluidTest {
  @Test
  fun `it should create an empty snapshot from an empty list of commands`() {
    // given
    val noCommands = emptyList<Command>()

    // when
    val snapshot = Fluid.createSnapshot(noCommands)

    // then
    assertThat(snapshot)
      .isEqualTo(Empty)
  }

  @Test
  fun `it should create a root directory on an in-memory snapshot`() {
    // given
    val commands = listOf(DirectoryCommand("root"))

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemory

    // then
    assertThat(snapshot)
      .hasDirectory("root")
  }
}
