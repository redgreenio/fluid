package io.redgreen.fluid.extensions

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.assist.getTestArtifact
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.nio.file.Files

class CryptoTest {
  @Test
  fun `it should compute a sha256 has for an input stream`() {
    // given
    val text = ""
    val inputStream = ByteArrayInputStream(text.toByteArray())

    // when
    val computedSha256 = computeSha256(inputStream)

    // then
    val sha256ForEmptyString = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
    assertThat(computedSha256)
      .isEqualTo(sha256ForEmptyString)
  }

  @Test
  fun `it should compute a sha256 for a file`() {
    // given
    val testArtifact = getTestArtifact("missing-generator-attribute.jar")
      .toAbsolutePath()

    // when
    val computedSha256 = computeSha256(Files.newInputStream(testArtifact))

    // then
    val hashFromSha256SumLinuxUtility = "4173d434df8b5e2d503920baf07847d8925d3f5e0c300b09072606a1351b9532"
    assertThat(computedSha256)
      .isEqualTo(hashFromSha256SumLinuxUtility)
  }
}
