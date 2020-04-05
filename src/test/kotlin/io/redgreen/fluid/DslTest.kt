package io.redgreen.fluid

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class DslTest {
  @Test
  fun `it should return an empty list for an empty scaffold`() {
    // given
    val scaffold = scaffold {
      /* empty */
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .isEmpty()
  }

  @Test
  fun `it should return a directory command for a directory call`() {
    // given
    val scaffold = scaffold {
      directory("src")
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        DirectoryCommand("src")
      )
  }

  @Test
  fun `it should return directory commands for nested directories`() {
    // given
    val scaffold = scaffold {
      directory("src") {
        directory("main")
      }
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        DirectoryCommand("src"),
        DirectoryCommand("src/main")
      )
      .inOrder()
  }

  @Test
  fun `it should return a file copy command for a file copy call`() {
    // given
    val scaffold = scaffold {
      fileCopy(".gitignore")
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        FileCopyCommand(".gitignore")
      )
  }
}
