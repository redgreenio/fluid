package io.redgreen.fluid.registry.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class RegistryHomeTest {
  @TempDir
  internal lateinit var supposedlyUserHome: File

  @Test
  fun `it can resolve a path to the fluid home directory`() {
    // when
    val registryHome = RegistryHome.from(supposedlyUserHome.toPath())

    // then
    val expectedPath = supposedlyUserHome.path + File.separator + ".fluid"
    assertThat(registryHome.path.toString())
      .isEqualTo(expectedPath)
  }
}
