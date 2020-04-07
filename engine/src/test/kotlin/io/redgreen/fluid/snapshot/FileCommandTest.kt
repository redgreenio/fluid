package io.redgreen.fluid.snapshot

import io.redgreen.fluid.FileCommand
import io.redgreen.fluid.Fluid
import io.redgreen.fluid.Resource
import io.redgreen.fluid.truth.InMemorySnapshotSubject.Companion.assertThat
import org.junit.jupiter.api.Test

class FileCommandTest {
  @Test
  fun `it should copy files from the source path`() {
    // given
    val commands = listOf(FileCommand("hello-world.txt"))

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemorySnapshot

    // then
    assertThat(snapshot)
      .hasFile("hello-world.txt")
    assertThat(snapshot)
      .hasFileWithContents("hello-world.txt", "how you doin'?")
  }

  @Test
  fun `it should copy file from an explicitly specified resource path`() {
    // given
    val commands = listOf(FileCommand(".gitignore", Resource("gitignore")))

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemorySnapshot

    // then
    assertThat(snapshot)
      .hasFileWithContents(".gitignore", "/build")
  }
}
