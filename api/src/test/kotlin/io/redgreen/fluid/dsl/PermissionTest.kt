package io.redgreen.fluid.dsl

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.dsl.Permission.EXECUTE
import io.redgreen.fluid.dsl.Permission.READ_WRITE
import org.junit.jupiter.api.Test

internal class PermissionTest {
  @Test
  fun `read write equals 6`() {
    assertThat(READ_WRITE)
      .isEqualTo(6)
  }

  @Test
  fun `execute equals 1`() {
    assertThat(EXECUTE)
      .isEqualTo(1)
  }
}
