package io.redgreen.fluid.dsl

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.commands.DirectoryCommand
import io.redgreen.fluid.commands.FileCopyCommand
import io.redgreen.fluid.commands.TemplateCommand
import org.junit.jupiter.api.Test

class DslTest {
  @Test
  fun `it should return an empty list for an empty scaffold`() {
    // given
    val scaffold = scaffold {
      /* empty */
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .isEmpty()
  }

  @Test
  fun `it should return a directory command for a directory call`() {
    // given
    val scaffold = scaffold {
      directory("src")
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        DirectoryCommand("src")
      )
  }

  @Test
  fun `it should return directory commands for nested directories`() {
    // given
    val scaffold = scaffold {
      directory("src") {
        directory("main")
      }
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        DirectoryCommand("src"),
        DirectoryCommand("src/main")
      )
      .inOrder()
  }

  @Test
  fun `it should return a file copy command for a file copy call`() {
    // given
    val scaffold = scaffold {
      fileCopy(".gitignore")
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        FileCopyCommand(".gitignore")
      )
  }

  @Test
  fun `it should return a file copy command for files nested inside directories`() {
    // given
    val scaffold = scaffold {
      directory("src/test") {
        fileCopy("CanaryTest.kt")
      }
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        DirectoryCommand("src/test"),
        FileCopyCommand("src/test/CanaryTest.kt")
      )
      .inOrder()
  }

  @Test
  fun `it should create a template command`() {
    // given
    val params = MultiModuleProject("bamboo-tools", "fluid")
    val scaffold = scaffold {
      template("settings.gradle", params)
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        TemplateCommand("settings.gradle", params)
      )
  }

  data class MultiModuleProject(
    val projectName: String,
    val moduleName: String
  )

  @Test
  fun `it should create template commands inside nested directories`() {
    // given
    val params = MultiModuleProject("bamboo-tools", "fluid")
    val scaffold = scaffold {
      directory("fluid") {
        template("fluid.iml", params)
      }
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        DirectoryCommand("fluid"),
        TemplateCommand("fluid/fluid.iml", params)
      ).inOrder()
  }

  @Test
  fun `it should work with all kinds of elements`() {
    // given
    val scaffold = scaffold {
      directory("one-directory")

      directory("directory") {
        directory("directory")
        fileCopy("file")

        directory("templates") {
          template("rocker.html", "Fluid")
        }
      }
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        DirectoryCommand("one-directory"),
        DirectoryCommand("directory"),
        DirectoryCommand("directory/directory"),
        FileCopyCommand("directory/file"),
        DirectoryCommand("directory/templates"),
        TemplateCommand("directory/templates/rocker.html", "Fluid")
      )
      .inOrder()
  }

  @Test
  fun `it should allow specifying resource path while copying files`() {
    // given
    val scaffold = scaffold {
      fileCopy(".gitignore", Resource("gitignore"))
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        FileCopyCommand(".gitignore", Resource("gitignore"))
      )
  }

  @Test
  fun `it should allow specifying resource path while copying files into directories`() {
    // given
    val scaffold = scaffold {
      directory("core") {
        fileCopy(".gitignore", Resource("gitignore"))
      }
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        DirectoryCommand("core"),
        FileCopyCommand("core/.gitignore", Resource("gitignore"))
      )
      .inOrder()
  }
}
