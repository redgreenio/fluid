package io.redgreen.fluid

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.Snapshot.InMemory
import io.redgreen.fluid.truth.InMemorySubject.Companion.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class FluidDirectoryTest {
  @Test
  fun `it should create an empty snapshot from an empty list of commands`() {
    // given
    val noCommands = emptyList<Command>()

    // when
    val exception = assertThrows<IllegalArgumentException> {
      Fluid.createSnapshot(noCommands)
    }

    // then
    assertThat(exception.message)
      .isEqualTo("'commands' should be a non-empty list.")
  }

  @Test
  fun `it should create a root directory in the in-memory snapshot`() {
    // given
    val commands = listOf(DirectoryCommand("root"))

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemory

    // then
    assertThat(snapshot)
      .hasDirectory("root")
  }

  @Test
  fun `it should create a all directories in the snapshot`() {
    // given
    val commands = listOf(DirectoryCommand("root/dir-level-1"))

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemory

    // then
    assertThat(snapshot)
      .hasDirectory("root/dir-level-1")
  }

  @Test
  fun `it should created nested directories in the snapshot`() {
    // given
    val commands = listOf(
      DirectoryCommand("root"),
      DirectoryCommand("root/dir-level-1"),
      DirectoryCommand("root/dir-level-1/dir-level-2")
    )

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemory

    // then
    assertThat(snapshot)
      .hasDirectory("root/dir-level-1/dir-level-2")
  }

  @Test
  fun `it should ignore multiple slashes and dots in the directory path`() {
    // given
    val commands = listOf(DirectoryCommand("root/./././/hello-world"))

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemory

    // then
    assertThat(snapshot)
      .hasDirectory("root/hello-world")
  }

  @Test
  fun `it should understand relative paths in the directory path`() {
    // given
    val commands = listOf(
      DirectoryCommand("root/child"),
      DirectoryCommand("root/../sibling")
    )

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemory

    // then
    assertThat(snapshot)
      .hasDirectories("root/child", "sibling")
  }

  @Test
  fun `it should create directories with spaces`() {
    // given
    val commands = listOf(DirectoryCommand("hello world"))

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemory

    // then
    assertThat(snapshot)
      .hasDirectory("hello world")
  }
}
