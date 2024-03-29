package io.redgreen.fluid.snapshot

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Source
import io.redgreen.fluid.snapshot.assist.buildSnapshot
import io.redgreen.fluid.testing.SnapshotSubject.Companion.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.FileNotFoundException

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
  fun `it should copy a template from an explicitly specified source path`() {
    // given
    val fileName = "greeting.txt"
    val model = "Ajay"
    val source = Source("messages/greeting.txt")

    // when
    val snapshot = TemplateCommand(fileName, model, source)
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

  @Test
  fun `it should throw an exception if the specified template is missing`() {
    // when
    val exception = assertThrows<FileNotFoundException> {
      TemplateCommand("missing-template.html", "Hello, world!")
        .buildSnapshot()
    }

    // then
    assertThat(exception.message)
      .isEqualTo("Unable to find template 'missing-template.html' in the generator's 'resources' directory.")
  }

  data class MultiModuleProject(
    val projectName: String,
    val moduleName: String
  )
}
