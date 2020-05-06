package io.redgreen.fluid.snapshot

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.Command
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.DirectoryEntry
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.dsl.Source
import io.redgreen.fluid.snapshot.assist.buildSnapshot
import org.junit.jupiter.api.Test

class GetEntriesTest {
  @Test
  fun `it should return an empty entry list when the snapshot is empty`() {
    // given
    val emptySnapshot = listOf<Command>().buildSnapshot()

    // when
    val entries = emptySnapshot.getEntries()

    // then
    assertThat(entries)
      .isEmpty()
  }

  @Test
  fun `it should return a directory entry for a directory path`() {
    // given
    val snapshotWithDirectory = DirectoryCommand("src")
      .buildSnapshot()

    // when
    val entries = snapshotWithDirectory.getEntries()

    // then
    assertThat(entries)
      .containsExactly(
        DirectoryEntry("src")
      )
  }

  @Test
  fun `it should return a file entry for a file path`() {
    // given
    val snapshotWithFile = FileCommand(".gitignore", Source("gitignore"))
      .buildSnapshot()

    // when
    val entries = snapshotWithFile.getEntries()

    // then
    assertThat(entries)
      .containsExactly(
        FileEntry(".gitignore")
      )
  }

  @Test
  fun `it should normalize paths and remove parent directories from entries`() {
    // given
    val snapshot = listOf(
      DirectoryCommand("src"),
      DirectoryCommand("src/main")
    ).buildSnapshot()

    // when
    val entries = snapshot.getEntries()

    // then
    assertThat(entries)
      .containsExactly(
        DirectoryEntry("src/main")
      )
  }
}
