package io.redgreen.fluid.cli.internal

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.registry.DefaultRegistry
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.nio.file.Paths

class InstallCommandArgsTest {
  private val registry = DefaultRegistry.from(Paths.get("/Users/Ajay"))
  private val installCommand = InstallCommand(registry)

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
