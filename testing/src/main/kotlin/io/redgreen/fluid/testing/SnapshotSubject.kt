package io.redgreen.fluid.testing

import com.google.common.truth.Fact.simpleFact
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.DirectoryEntry
import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.api.FileSystemEntry
import io.redgreen.fluid.api.Snapshot
import java.io.InputStream

class SnapshotSubject(
  metadata: FailureMetadata,
  private val actual: Snapshot
) : Subject(metadata, actual) {
  companion object {
    private val snapshotSubjects = Factory(::SnapshotSubject)

    @JvmStatic
    fun assertThat(snapshot: Snapshot): SnapshotSubject =
      assertAbout(snapshotSubjects).that(snapshot)
  }

  fun has(entry: FileSystemEntry) {
    if (!actual.getEntries().contains(entry)) {
      val entryName = getReadableEntryName(entry)
      failWithoutActual(
        simpleFact("expected: a $entryName at '${entry.path}'"),
        simpleFact("but was : not found")
      )
    }
  }

  fun hasFileWithContents(path: String, contents: String) {
    assertFileWithContents(path) { inputStream ->
      assertThat(inputStream.reader().readText())
        .isEqualTo(contents)
    }
  }

  fun hasFileWithContents(path: String, bytes: ByteArray) {
    assertFileWithContents(path) { inputStream ->
      assertThat(inputStream.readBytes())
        .isEqualTo(bytes)
    }
  }

  fun hasExactly(
    entry: FileSystemEntry,
    vararg entries: FileSystemEntry
  ) {
    @Suppress("SpreadOperator")
    assertThat(actual.getEntries())
      .containsExactly(entry, *entries)
  }

  private fun getReadableEntryName(
    entry: FileSystemEntry
  ): String = when (entry) {
    is FileEntry -> "file"
    is DirectoryEntry -> "directory"
  }

  private fun assertFileWithContents(
    path: String,
    contentsAssert: (InputStream) -> Unit
  ) {
    has(FileEntry(path))
    val inputStreamOptional = actual.inputStream(path)
    inputStreamOptional.get().use { inputStream ->
      contentsAssert(inputStream)
    }
  }
}
