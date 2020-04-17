package io.redgreen.fluid.registry.domain

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.registry.model.VersionComparison
import io.redgreen.fluid.registry.model.VersionComparison.EQUAL
import io.redgreen.fluid.registry.model.VersionComparison.NA
import io.redgreen.fluid.registry.model.VersionComparison.NEWER
import io.redgreen.fluid.registry.model.VersionComparison.OLDER
import org.junit.jupiter.api.Test

internal class VersionComparisonTest {
  @Test
  fun `it should detect a newer version`() {
    assertThat(VersionComparison.compareCandidate("1.0.0", "1.1.0"))
      .isEqualTo(NEWER)
  }

  @Test
  fun `it should detect an older version`() {
    assertThat(VersionComparison.compareCandidate("1.1.0", "1.0.0"))
      .isEqualTo(OLDER)
  }

  @Test
  fun `it should detect equal version numbers`() {
    assertThat(VersionComparison.compareCandidate("1.0.0", "1.0.0"))
      .isEqualTo(EQUAL)
  }

  @Test
  fun `it should return NA for incomparable version numbers`() {
    assertThat(VersionComparison.compareCandidate("Unknown", "1.0.0"))
      .isEqualTo(NA)
  }
}
