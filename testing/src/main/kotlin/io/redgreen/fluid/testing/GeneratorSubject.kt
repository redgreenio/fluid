package io.redgreen.fluid.testing

import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.FileSystemEntry
import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.snapshot.InMemorySnapshot

class GeneratorSubject(
  metadata: FailureMetadata,
  private val actual: Generator
) : Subject(metadata, actual) {
  companion object {
    private val generatorSubjects = Factory(::GeneratorSubject)

    @JvmStatic
    fun assertThat(generator: Generator): GeneratorSubject {
      return assertAbout(generatorSubjects).that(generator)
    }
  }

  fun generatesExactly(
    entry: FileSystemEntry,
    vararg entries: FileSystemEntry
  ) {
    val snapshot = InMemorySnapshot.forGenerator(actual::class.java)
    val commands = actual.scaffold().prepare()

    commands.onEach { command ->
      when (command) {
        is DirectoryCommand -> snapshot.execute(command)
        is FileCommand -> snapshot.execute(command)
        is TemplateCommand<*> -> snapshot.execute(command)
      }
    }

    assertThat(snapshot.getEntries())
      .containsExactly(entry, *entries)
  }
}
