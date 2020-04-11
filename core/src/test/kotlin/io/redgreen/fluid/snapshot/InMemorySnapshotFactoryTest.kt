package io.redgreen.fluid.snapshot

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.snapshot.assist.NoOpGenerator
import io.redgreen.fluid.testing.SnapshotSubject.Companion.assertThat
import org.junit.jupiter.api.Test

class InMemorySnapshotFactoryTest {
  @Test
  fun `it should create a new instance of snapshot every time`() {
    // given
    val snapshotFactory = InMemorySnapshotFactory()

    // when
    val generatorClass = NoOpGenerator::class.java as Class<Generator>
    val snapshotA = snapshotFactory.newInstance(generatorClass)
    val snapshotB = snapshotFactory.newInstance(generatorClass)

    // then
    assertThat(snapshotA)
      .isNotSameInstanceAs(snapshotB)
  }
}
