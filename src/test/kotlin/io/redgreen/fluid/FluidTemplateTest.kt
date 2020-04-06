package io.redgreen.fluid

import io.redgreen.fluid.Snapshot.InMemory
import io.redgreen.fluid.commands.TemplateCommand
import io.redgreen.fluid.dsl.Resource
import io.redgreen.fluid.model.MultiModuleProject
import io.redgreen.fluid.truth.InMemorySubject.Companion.assertThat
import org.junit.jupiter.api.Test

class FluidTemplateTest {
  @Test
  fun `it should copy a template from the source path`() {
    // given
    val projectName = "bamboo-tools"
    val moduleName = "fluid"
    val commands = listOf(
      TemplateCommand("settings.gradle", MultiModuleProject(projectName, moduleName))
    )

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemory

    // when
    val settingsGradle = """
        rootProject.name = '$projectName'
        include '$moduleName'
      """.trimIndent()
    assertThat(snapshot)
      .hasFileWithContents("settings.gradle", settingsGradle)
  }

  @Test
  fun `it should copy a template from an explicitly specified resource path`() {
    // given
    val commands = listOf(
      TemplateCommand("greeting.txt", "Ajay", Resource("messages/greeting.txt"))
    )

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemory

    // then
    assertThat(snapshot)
      .hasFileWithContents("greeting.txt", "Hello, Ajay!")
  }
}
