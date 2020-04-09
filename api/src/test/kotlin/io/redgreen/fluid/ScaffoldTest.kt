package io.redgreen.fluid

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.dsl.scaffold
import org.junit.jupiter.api.Test

class ScaffoldTest {
  @Test
  fun `it should ensure prepare call is idempotent`() {
    // given
    val scaffold = scaffold {
      dir("src")
    }

    // when
    val commands = with(scaffold) {
      prepare()
      prepare()
      prepare()
      prepare()
    }

    // then
    assertThat(commands)
      .containsExactly(DirectoryCommand("src"))
  }
}
