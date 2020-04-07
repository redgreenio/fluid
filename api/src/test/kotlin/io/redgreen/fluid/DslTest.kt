package io.redgreen.fluid

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Resource
import io.redgreen.fluid.dsl.scaffold
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DslTest {
  @Test
  fun `it throws an exception for an empty scaffold`() {
    // given
    val scaffold = scaffold {
      /* empty */
    }

    // when
    val exception = assertThrows<IllegalStateException> {
      scaffold.prepare()
    }

    // then
    val errorMessage = "The scaffold is empty. You can make it useful by " +
      "creating directories, copying files, or templates."
    assertThat(exception.message)
      .isEqualTo(errorMessage)
  }

  @Test
  fun `it should return a directory command for a directory call`() {
    // given
    val scaffold = scaffold {
      dir("src")
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
      dir("src") {
        dir("main")
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
      file(".gitignore")
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        FileCommand(".gitignore")
      )
  }

  @Test
  fun `it should return a file copy command for files nested inside directories`() {
    // given
    val scaffold = scaffold {
      dir("src/test") {
        file("CanaryTest.kt")
      }
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        DirectoryCommand("src/test"),
        FileCommand("src/test/CanaryTest.kt")
      )
      .inOrder()
  }

  @Test
  fun `it should create a template command`() {
    // given
    val model = MultiModuleProject("bamboo-tools", "fluid")
    val scaffold = scaffold {
      template("settings.gradle", model)
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        TemplateCommand("settings.gradle", model)
      )
  }

  @Test
  fun `it should create template commands inside nested directories`() {
    // given
    val model = MultiModuleProject("bamboo-tools", "fluid")
    val scaffold = scaffold {
      dir("fluid") {
        template("fluid.iml", model)
      }
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        DirectoryCommand("fluid"),
        TemplateCommand("fluid/fluid.iml", model)
      ).inOrder()
  }

  @Test
  fun `it should work with all kinds of elements`() {
    // given
    val scaffold = scaffold {
      dir("one-directory")

      dir("directory") {
        dir("directory")
        file("file")

        dir("templates") {
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
        FileCommand("directory/file"),
        DirectoryCommand("directory/templates"),
        TemplateCommand("directory/templates/rocker.html", "Fluid")
      )
      .inOrder()
  }

  @Test
  fun `it should allow specifying resource path while copying files`() {
    // given
    val scaffold = scaffold {
      file(".gitignore", Resource("gitignore"))
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        FileCommand(".gitignore", Resource("gitignore"))
      )
  }

  @Test
  fun `it should allow specifying resource path while copying files into directories`() {
    // given
    val scaffold = scaffold {
      dir("core") {
        file(".gitignore", Resource("gitignore"))
      }
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        DirectoryCommand("core"),
        FileCommand("core/.gitignore", Resource("gitignore"))
      )
      .inOrder()
  }

  @Test
  fun `it should create template commands (root) with resource`() {
    // given
    val model = MultiModuleProject("bamboo-tools", "fluid")
    val scaffold = scaffold {
      template("build.gradle", model, Resource("templates/build.gradle"))
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        TemplateCommand("build.gradle", model, Resource("templates/build.gradle"))
      )
  }

  @Test
  fun `it should create template commands (nested directory) with resource`() {
    // given
    val model = MultiModuleProject("bamboo-tools", "fluid")
    val scaffold = scaffold {
      dir("fluid") {
        template("build.gradle", model, Resource("templates/build.gradle"))
      }
    }

    // when
    val commands = scaffold.prepare()

    // then
    assertThat(commands)
      .containsExactly(
        DirectoryCommand("fluid"),
        TemplateCommand("fluid/build.gradle", model, Resource("templates/build.gradle"))
      )
  }

  data class MultiModuleProject(
    val projectName: String,
    val moduleName: String
  )
}
