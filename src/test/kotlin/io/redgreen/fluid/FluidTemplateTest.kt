package io.redgreen.fluid

import io.redgreen.fluid.Snapshot.InMemory
import io.redgreen.fluid.commands.TemplateCommand
import io.redgreen.fluid.model.MultiModuleProject
import io.redgreen.fluid.truth.InMemorySubject.Companion.assertThat
import org.junit.jupiter.api.Test

class FluidTemplateTest {
  @Test
  fun `it should copy a template from the source directory`() {
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
}
