package io.redgreen.fluid.truth

import com.google.common.truth.Fact.simpleFact
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import io.redgreen.fluid.Snapshot.InMemory

class InMemorySubject(
  metadata: FailureMetadata,
  private val actual: InMemory
) : Subject(metadata, actual) {
  companion object {
    private val inMemoryInstances = Factory<InMemorySubject, InMemory> { metadata, actual ->
      InMemorySubject(metadata, actual)
    }

    @JvmStatic
    fun assertThat(inMemory: InMemory): InMemorySubject {
      return assertAbout(inMemoryInstances).that(inMemory)
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
    paths.toList().onEach(this@InMemorySubject::hasDirectory)
  }
}
