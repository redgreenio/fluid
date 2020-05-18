package io.redgreen.fluid.snapshot

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Permission.EXECUTE
import io.redgreen.fluid.dsl.Source
import io.redgreen.fluid.dsl.Source.Companion.MIRROR_DESTINATION
import io.redgreen.fluid.snapshot.assist.buildSnapshot
import io.redgreen.fluid.testing.SnapshotSubject.Companion.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

class FileCommandTest {
  @Test
  fun `it should copy files from the source path`() {
    // when
    val snapshot = FileCommand("hello-world.txt").buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(FileEntry("hello-world.txt"))
    assertThat(snapshot)
      .hasFileWithContents("hello-world.txt", "how you doin'?")
  }

  @Test
  fun `it should copy file from an explicitly specified source path`() {
    // when
    val snapshot = FileCommand(".gitignore", Source("gitignore"))
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(FileEntry(".gitignore"))
    assertThat(snapshot)
      .hasFileWithContents(".gitignore", "/build")
  }

  @Test
  fun `it should copy binary files from the source path`() {
    // given
    val strawberryBytes = this::class.java.classLoader
      .getResourceAsStream("strawberry.png")!!
      .readBytes()

    // when
    val snapshot = FileCommand("strawberry.png")
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(FileEntry("strawberry.png"))
    assertThat(snapshot)
      .hasFileWithContents("strawberry.png", strawberryBytes)
  }

  @Test
  fun `it should copy files from a directory path`() {
    // when
    val snapshot = FileCommand("docs/doc1.txt", Source("docs/doc1.txt"))
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .has(FileEntry("docs/doc1.txt"))
  }

  @Test
  fun `it should create a file with execute permission`() {
    // when
    val snapshot = FileCommand("say-hello.sh", MIRROR_DESTINATION, EXECUTE)
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .has(FileEntry("say-hello.sh", EXECUTE))
  }

  @Test
  fun `it should create a template with execute permission`() {
    // when
    val snapshot = TemplateCommand("say-hello.sh", Unit, MIRROR_DESTINATION, EXECUTE)
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .has(FileEntry("say-hello.sh", EXECUTE))
  }

  @Test
  fun `it should throw an exception if the specified file is missing`() {
    // when
    val exception = assertThrows<FileNotFoundException> {
      FileCommand("missing-file.txt")
        .buildSnapshot()
    }

    // then
    assertThat(exception.message)
      .isEqualTo("Unable to find file 'missing-file.txt' in the generator's 'resources' directory.")
  }
}
