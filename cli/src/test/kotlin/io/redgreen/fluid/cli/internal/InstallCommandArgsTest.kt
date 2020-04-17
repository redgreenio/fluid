package io.redgreen.fluid.cli.internal

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.nio.file.Paths

class InstallCommandArgsTest {
  private val installCommand = InstallCommand(Paths.get("/Users/Ajay"))

  @Test
  fun `it should parse args for install jar command (short form)`() {
    // given
    val args = arrayOf("-j", "my-generator.jar")

    // when
    CommandLine(installCommand).parseArgs(*args)

    // then
    assertThat(installCommand.candidatePath)
      .isEqualTo(Paths.get("my-generator.jar"))
  }

  @Test
  fun `it should parse args for install jar command (longer form)`() {
    // given
    val args = arrayOf("--jar", "my-generator.jar")

    // when
    CommandLine(installCommand).parseArgs(*args)

    // then
    assertThat(installCommand.candidatePath)
      .isEqualTo(Paths.get("my-generator.jar"))
  }
}
