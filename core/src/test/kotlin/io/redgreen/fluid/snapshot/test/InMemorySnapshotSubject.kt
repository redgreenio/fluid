package io.redgreen.fluid.snapshot.test

import com.google.common.truth.Fact.simpleFact
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.snapshot.InMemorySnapshot

class InMemorySnapshotSubject(
  metadata: FailureMetadata,
  private val actual: InMemorySnapshot
) : Subject(metadata, actual) {
  companion object {
    private val inMemorySnapshots = Factory<InMemorySnapshotSubject, InMemorySnapshot> { metadata, actual ->
      InMemorySnapshotSubject(metadata, actual)
    }

    @JvmStatic
    fun assertThat(inMemorySnapshot: InMemorySnapshot): InMemorySnapshotSubject {
      return assertAbout(inMemorySnapshots).that(inMemorySnapshot)
    }
  }

  fun hasDirectory(path: String) {
    if (!actual.directoryExists(path)) {
      failWithoutActual(
        simpleFact("expected: a directory '$path'"),
        simpleFact("but was : not found")
      )
    }
  }

  fun hasDirectories(vararg paths: String) {
    paths.toList().onEach(this@InMemorySnapshotSubject::hasDirectory)
  }

  fun hasFile(path: String) {
    if (!actual.fileExists(path)) {
      failWithoutActual(
        simpleFact("expected: a file '$path'"),
        simpleFact("but was : not found")
      )
    }
  }

  fun hasFileWithContents(
    path: String,
    contents: String
  ) {
    hasFile(path)
    val actualContents = actual.readText(path)
    assertThat(actualContents)
      .isEqualTo(contents)
  }
}