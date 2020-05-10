package io.redgreen.fluid.testing

import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import io.redgreen.fluid.api.FileSystemEntry
import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.snapshot.InMemorySnapshotFactory
import io.redgreen.fluid.testing.SnapshotSubject.Companion.assertThat

class GeneratorSubject(
  metadata: FailureMetadata,
  private val actual: Generator<*>
) : Subject(metadata, actual) {
  companion object {
    private val generatorSubjects = Factory(::GeneratorSubject)

    @JvmStatic
    fun assertThat(generator: Generator<*>): GeneratorSubject {
      return assertAbout(generatorSubjects).that(generator)
    }
  }

  private val snapshot by lazy {
    val snapshotFactory = InMemorySnapshotFactory()
    val snapshotParams = actual::class.java.asSubclass(Generator::class.java)
    val dslConfig = actual.configure()
    actual
      .scaffold()
      .buildSnapshot(snapshotFactory, snapshotParams, dslConfig)
  }

  fun generatesExactly(
    entry: FileSystemEntry,
    vararg entries: FileSystemEntry
  ) {
    assertThat(snapshot)
      .hasExactly(entry, *entries)
  }

  fun generatesFileWithContent(path: String, content: String) {
    assertThat(snapshot)
      .hasFileWithContents(path, content)
  }

  fun generatesFileWithContent(path: String, bytes: ByteArray) {
    assertThat(snapshot)
      .hasFileWithContents(path, bytes)
  }
}
