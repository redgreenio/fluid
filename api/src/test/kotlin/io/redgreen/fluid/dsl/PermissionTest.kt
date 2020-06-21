package io.redgreen.fluid.dsl

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.dsl.Permission.EXECUTE
import io.redgreen.fluid.dsl.Permission.R
import io.redgreen.fluid.dsl.Permission.READ_WRITE
import io.redgreen.fluid.dsl.Permission.W
import io.redgreen.fluid.dsl.Permission.X
import org.junit.jupiter.api.Test

internal class PermissionTest {
  @Test
  fun `read write equals 6`() {
    assertThat(READ_WRITE)
      .isEqualTo(R or W)
  }

  @Test
  fun `execute equals 7`() {
    assertThat(EXECUTE)
      .isEqualTo(R or W or X)
  }
}
