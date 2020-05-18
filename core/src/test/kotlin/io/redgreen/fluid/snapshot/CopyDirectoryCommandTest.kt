package io.redgreen.fluid.snapshot

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.CopyDirectoryCommand
import io.redgreen.fluid.api.DirectoryEntry
import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.dsl.Source
import io.redgreen.fluid.snapshot.assist.buildSnapshot
import io.redgreen.fluid.testing.SnapshotSubject.Companion.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

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
    val exception = assertThrows<FileNotFoundException> {
      CopyDirectoryCommand("non-existent-directory")
        .buildSnapshot()
    }

    // then
    assertThat(exception.message)
      .isEqualTo("Unable to find 'non-existent-directory' in the generator's 'resources' directory.")
  }

  @Test
  fun `it should copy existing directories and files within`() {
    // when
    val snapshot = CopyDirectoryCommand("docs")
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(
        DirectoryEntry("docs"),
        FileEntry("docs/doc1.txt"),
        FileEntry("docs/doc2.txt")
      )
  }

  @Test
  fun `it should copy contents of nested directories`() {
    // when
    val snapshot = CopyDirectoryCommand("directories")
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly( // FIXME, why doesn't this assertion contain `DirectoryEntry("directories")`
        FileEntry("directories/file1.txt"),
        DirectoryEntry("directories/nested"),
        FileEntry("directories/nested/another-file.txt")
      )
  }

  @Test
  fun `is should copy existing directories and files with explicit source`() {
    // when
    val snapshot = CopyDirectoryCommand("documentation", Source("docs"))
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(
        DirectoryEntry("documentation"),
        FileEntry("documentation/doc1.txt"),
        FileEntry("documentation/doc2.txt")
      )
  }

  @Test
  fun `it should copy empty directories with explicit source`() {
    // when
    val snapshot = CopyDirectoryCommand("black-hole", Source("empty-directory"))
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(
        DirectoryEntry("black-hole")
      )
  }
}
