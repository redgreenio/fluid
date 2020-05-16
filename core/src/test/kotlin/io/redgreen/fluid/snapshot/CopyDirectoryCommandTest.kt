package io.redgreen.fluid.snapshot

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.CopyDirectoryCommand
import io.redgreen.fluid.api.DirectoryEntry
import io.redgreen.fluid.snapshot.assist.buildSnapshot
import io.redgreen.fluid.testing.SnapshotSubject.Companion.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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

  @Test
  fun `it should throw an exception if the specified directory is missing`() {
    // when
    val exception = assertThrows<IllegalStateException> {
      CopyDirectoryCommand("non-existent-directory")
        .buildSnapshot()
    }

    // then
    assertThat(exception.message)
      .isEqualTo("Unable to find 'non-existent-directory' in the generator's 'resources' directory.")
  }
}
