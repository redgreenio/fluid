package io.redgreen.fluid.snapshot

import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.DirectoryEntry
import io.redgreen.fluid.snapshot.assist.buildSnapshot
import io.redgreen.fluid.testing.SnapshotSubject.Companion.assertThat
import org.junit.jupiter.api.Test

class DirectoryCommandTest {
  @Test
  fun `it should create a root directory in the in-memory snapshot`() {
    // when
    val snapshot = DirectoryCommand("root")
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(DirectoryEntry("root"))
  }

  @Test
  fun `it should create a all directories in the snapshot`() {
    // when
    val snapshot = DirectoryCommand("root/dir-level-1")
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(DirectoryEntry("root/dir-level-1"))
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
    val snapshot = commands.buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(DirectoryEntry("root/dir-level-1/dir-level-2"))
  }

  @Test
  fun `it should ignore multiple slashes and dots in the directory path`() {
    // when
    val snapshot = DirectoryCommand("root/./././/hello-world")
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(DirectoryEntry("root/hello-world"))
  }

  @Test
  fun `it should understand relative paths in the directory path`() {
    // given
    val commands = listOf(
      DirectoryCommand("root/child"),
      DirectoryCommand("root/../sibling")
    )

    // when
    val snapshot = commands.buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(
        DirectoryEntry("root/child"),
        DirectoryEntry("sibling")
      )
  }

  @Test
  fun `it should create directories with spaces`() {
    // when
    val snapshot = DirectoryCommand("hello world")
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(DirectoryEntry("hello world"))
  }
}
