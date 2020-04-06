package io.redgreen.fluid

import io.redgreen.fluid.Snapshot.InMemory
import io.redgreen.fluid.commands.FileCopyCommand
import io.redgreen.fluid.dsl.Resource
import io.redgreen.fluid.truth.InMemorySubject.Companion.assertThat
import org.junit.jupiter.api.Test

class FluidFileCopyTest {
  @Test
  fun `it should copy files from the source path`() {
    // given
    val commands = listOf(FileCopyCommand("hello-world.txt"))

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemory

    // then
    assertThat(snapshot)
      .hasFile("hello-world.txt")
    assertThat(snapshot)
      .hasFileWithContents("hello-world.txt", "how you doin'?")
  }

  @Test
  fun `it should copy file from an explicilty specified resource path`() {
    // given
    val commands = listOf(FileCopyCommand(".gitignore", Resource("gitignore")))

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemory

    // then
    assertThat(snapshot)
      .hasFileWithContents(".gitignore", "/build")
  }
}
