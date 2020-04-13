package io.redgreen.fluid.cli.internal

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.nio.file.Path

class InstallCommandArgsTest {
  private val installCommand = InstallCommand(Path.of("/Users/Ajay"))

  @Test
  fun `it should parse args for install jar command (short form)`() {
    // given
    val args = arrayOf("-j", "my-generator.jar")

    // when
    CommandLine(installCommand).parseArgs(*args)

    // then
    assertThat(installCommand.artifactPath)
      .isEqualTo(Path.of("my-generator.jar"))
  }

  @Test
  fun `it should parse args for install jar command (longer form)`() {
    // given
    val args = arrayOf("--jar", "my-generator.jar")

    // when
    CommandLine(installCommand).parseArgs(*args)

    // then
    assertThat(installCommand.artifactPath)
      .isEqualTo(Path.of("my-generator.jar"))
  }
}
