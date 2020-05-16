package io.redgreen.fluid.snapshot

import io.redgreen.fluid.api.CopyDirectoryCommand
import io.redgreen.fluid.api.DirectoryEntry
import io.redgreen.fluid.snapshot.assist.buildSnapshot
import io.redgreen.fluid.testing.SnapshotSubject.Companion.assertThat
import org.junit.jupiter.api.Test

class CopyDirectoryCommandTest {
  @Test
  fun `it should copy an empty directory`() {
    // when
    val snapshot = CopyDirectoryCommand("empty-directory")
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(
        DirectoryEntry("empty-directory")
      )
  }
}
