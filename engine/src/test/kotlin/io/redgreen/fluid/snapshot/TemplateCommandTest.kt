package io.redgreen.fluid.snapshot

import io.redgreen.fluid.Fluid
import io.redgreen.fluid.Resource
import io.redgreen.fluid.TemplateCommand
import io.redgreen.fluid.model.MultiModuleProject
import io.redgreen.fluid.truth.InMemorySnapshotSubject.Companion.assertThat
import org.junit.jupiter.api.Test

class TemplateCommandTest {
  @Test
  fun `it should copy a template from the source path`() {
    // given
    val projectName = "bamboo-tools"
    val moduleName = "fluid"
    val commands = listOf(
      TemplateCommand("settings.gradle", MultiModuleProject(projectName, moduleName))
    )

    // when
    val snapshot = Fluid.createSnapshot(commands) as InMemorySnapshot

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
    val snapshot = Fluid.createSnapshot(commands) as InMemorySnapshot

    // then
    assertThat(snapshot)
      .hasFileWithContents("greeting.txt", "Hello, Ajay!")
  }
}
