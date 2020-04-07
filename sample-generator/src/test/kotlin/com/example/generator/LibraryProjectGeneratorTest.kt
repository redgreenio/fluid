package com.example.generator

import io.redgreen.fluid.api.DirectoryEntry
import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.testing.GeneratorSubject.Companion.assertThat
import org.junit.jupiter.api.Test

class LibraryProjectGeneratorTest {
  @Test
  fun `it should generate a library project`() {
    // given
    val generator = LibraryProjectGenerator()

    // when & then
    assertThat(generator)
      .generatesExactly(
        DirectoryEntry("src/main/java"),
        DirectoryEntry("src/main/kotlin"),
        DirectoryEntry("src/test/java"),
        DirectoryEntry("src/test/kotlin"),
        FileEntry(".gitignore"),
        FileEntry("build.gradle")
      )
  }
}
