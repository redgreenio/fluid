package io.redgreen.fluid.snapshot

import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.dsl.Resource
import io.redgreen.fluid.snapshot.test.InMemorySnapshotSubject.Companion.assertThat
import io.redgreen.fluid.snapshot.test.buildSnapshot
import org.junit.jupiter.api.Test

class FileCommandTest {
  @Test
  fun `it should copy files from the source path`() {
    // when
    val snapshot = FileCommand("hello-world.txt").buildSnapshot()

    // then
    assertThat(snapshot)
      .hasFile("hello-world.txt")
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
      .hasFileWithContents("strawberry.png", strawberryBytes)
  }
}
