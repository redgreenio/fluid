package io.redgreen.fluid.registry.domain

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.redgreen.fluid.registry.Registry
import io.redgreen.fluid.registry.domain.ListInstalledGeneratorsUseCase.Result.InstalledGenerators
import io.redgreen.fluid.registry.domain.ListInstalledGeneratorsUseCase.Result.NoGeneratorsInstalled
import io.redgreen.fluid.registry.model.InstalledGenerator
import io.redgreen.fluid.registry.model.RegistryEntry
import org.junit.jupiter.api.Test

class ListInstalledGeneratorsUseCaseTest {
  private val registry = mock<Registry>()
  private val useCase = ListInstalledGeneratorsUseCase(registry)

  @Test
  fun `it should return no installed generators when no generators are installed`() {
    assertThat(useCase.invoke())
      .isEqualTo(NoGeneratorsInstalled)
  }

  @Test
  fun `it should return a list of installed generators when generators are installed`() {
    // given
    val entries = listOf(
      RegistryEntry("bootstrapper", "bootstrapper-1.0.jar"),
      RegistryEntry("kotlin-mmp", "kotlin-mmp-1.0.jar")
    )

    whenever(registry.getEntries())
      .thenReturn(entries)

    // when & then
    val generators = entries.map { InstalledGenerator(it.id) }
    assertThat(useCase.invoke())
      .isEqualTo(InstalledGenerators(generators))
  }
}
