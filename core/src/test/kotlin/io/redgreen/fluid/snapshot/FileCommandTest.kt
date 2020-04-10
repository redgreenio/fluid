package io.redgreen.fluid.snapshot

import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.dsl.Resource
import io.redgreen.fluid.snapshot.test.buildSnapshot
import io.redgreen.fluid.testing.SnapshotSubject.Companion.assertThat
import org.junit.jupiter.api.Test

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
  fun `it should copy file from an explicitly specified resource path`() {
    // when
    val snapshot = FileCommand(".gitignore", Resource("gitignore"))
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
      .readAllBytes()

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
    val snapshot = FileCommand("docs/doc1.txt", Resource("docs/doc1.txt"))
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .has(FileEntry("docs/doc1.txt"))
  }
}
