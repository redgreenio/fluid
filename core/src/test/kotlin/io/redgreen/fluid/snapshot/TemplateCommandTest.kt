package io.redgreen.fluid.snapshot

import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Resource
import io.redgreen.fluid.snapshot.test.buildSnapshot
import io.redgreen.fluid.testing.SnapshotSubject.Companion.assertThat
import org.junit.jupiter.api.Test

class TemplateCommandTest {
  @Test
  fun `it should copy a template from the source path`() {
    // given
    val projectName = "bamboo-tools"
    val moduleName = "fluid"
    val model = MultiModuleProject(projectName, moduleName)

    // when
    val snapshot = TemplateCommand("settings.gradle", model)
      .buildSnapshot()

    // then
    val contents = """
        rootProject.name = '$projectName'
        include '$moduleName'
      """.trimIndent()

    assertThat(snapshot)
      .hasExactly(FileEntry("settings.gradle"))
    assertThat(snapshot)
      .hasFileWithContents("settings.gradle", contents)
  }

  @Test
  fun `it should copy a template from an explicitly specified resource path`() {
    // given
    val fileName = "greeting.txt"
    val model = "Ajay"
    val resource = Resource("messages/greeting.txt")

    // when
    val snapshot = TemplateCommand(fileName, model, resource)
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasExactly(FileEntry(fileName))
    assertThat(snapshot)
      .hasFileWithContents(fileName, "Hello, Ajay!")
  }

  @Test
  fun `it should copy templates from a directory path`() {
    // when
    val snapshot = TemplateCommand("messages/greeting.txt", "Ajay")
      .buildSnapshot()

    // then
    assertThat(snapshot)
      .hasFileWithContents("messages/greeting.txt", "Hello, Ajay!")
  }

  data class MultiModuleProject(
    val projectName: String,
    val moduleName: String
  )
}
