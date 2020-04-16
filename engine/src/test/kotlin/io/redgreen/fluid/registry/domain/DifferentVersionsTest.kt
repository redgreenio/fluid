package io.redgreen.fluid.registry.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentVersions
import io.redgreen.fluid.registry.model.VersionComparison.EQUAL
import io.redgreen.fluid.registry.model.VersionComparison.NA
import io.redgreen.fluid.registry.model.VersionComparison.NEWER
import io.redgreen.fluid.registry.model.VersionComparison.OLDER
import org.junit.jupiter.api.Test

internal class DifferentVersionsTest {
  @Test
  fun `it should detect a newer version`() {
    // given
    val differentVersions = DifferentVersions("1.0.0", "1.1.0")

    // when
    val comparison = differentVersions.compare()

    // then
    assertThat(comparison)
      .isEqualTo(NEWER)
  }

  @Test
  fun `it should detect an older version`() {
    // given
    val differentVersions = DifferentVersions("1.1.0", "1.0.0")

    // when
    val comparison = differentVersions.compare()

    // then
    assertThat(comparison)
      .isEqualTo(OLDER)
  }

  @Test
  fun `it should detect equal version numbers`() {
    // given
    val differentVersions = DifferentVersions("1.0.0", "1.0.0")

    // when
    val comparison = differentVersions.compare()

    // then
    assertThat(comparison)
      .isEqualTo(EQUAL)
  }

  @Test
  fun `it should return NA for incomparable version numbers`() {
    // given
    val differentVersions = DifferentVersions("Unknown", "1.0.0")

    // when
    val comparison = differentVersions.compare()

    // then
    assertThat(comparison)
      .isEqualTo(NA)
  }
}
