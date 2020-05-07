package io.redgreen.fluid

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.dsl.Permission.EXECUTE
import io.redgreen.fluid.dsl.Source
import io.redgreen.fluid.dsl.Source.Companion.MIRROR_DESTINATION
import io.redgreen.fluid.dsl.scaffold
import org.junit.jupiter.api.Nested
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
      scaffold.transformDslToCommands()
    }

    // then
    val errorMessage = "The scaffold is empty. You can make it useful by " +
      "creating directories, copying files, or templates."
    assertThat(exception.message)
      .isEqualTo(errorMessage)
  }

  @Nested
  inner class Directory {
    @Test
    fun `it should return a directory command for a directory call`() {
      // given
      val scaffold = scaffold {
        dir("src")
      }

      // when
      val commands = scaffold.transformDslToCommands()

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
      val commands = scaffold.transformDslToCommands()

      // then
      assertThat(commands)
        .containsExactly(
          DirectoryCommand("src"),
          DirectoryCommand("src/main")
        )
        .inOrder()
    }
  }

  @Nested
  inner class File {
    @Test
    fun `it should return a file copy command for a file copy call`() {
      // given
      val scaffold = scaffold {
        file(".gitignore")
      }

      // when
      val commands = scaffold.transformDslToCommands()

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
      val commands = scaffold.transformDslToCommands()

      // then
      assertThat(commands)
        .containsExactly(
          DirectoryCommand("src/test"),
          FileCommand("src/test/CanaryTest.kt")
        )
        .inOrder()
    }
  }

  @Nested
  inner class Template {
    @Test
    fun `it should create a template command`() {
      // given
      val model = MultiModuleProject("bamboo-tools", "fluid")
      val scaffold = scaffold {
        template("settings.gradle", model)
      }

      // when
      val commands = scaffold.transformDslToCommands()

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
      val commands = scaffold.transformDslToCommands()

      // then
      assertThat(commands)
        .containsExactly(
          DirectoryCommand("fluid"),
          TemplateCommand("fluid/fluid.iml", model)
        ).inOrder()
    }
  }

  @Nested
  inner class ExplicitSourceInFile {
    @Test
    fun `it should allow specifying source path while copying files`() {
      // given
      val scaffold = scaffold {
        file(".gitignore", Source("gitignore"))
      }

      // when
      val commands = scaffold.transformDslToCommands()

      // then
      assertThat(commands)
        .containsExactly(
          FileCommand(".gitignore", Source("gitignore"))
        )
    }

    @Test
    fun `it should allow specifying source path while copying files into directories`() {
      // given
      val scaffold = scaffold {
        dir("core") {
          file(".gitignore", Source("gitignore"))
        }
      }

      // when
      val commands = scaffold.transformDslToCommands()

      // then
      assertThat(commands)
        .containsExactly(
          DirectoryCommand("core"),
          FileCommand("core/.gitignore", Source("gitignore"))
        )
        .inOrder()
    }
  }

  @Nested
  inner class ExplicitSourceInTemplate {
    @Test
    fun `it should create template commands (root) with source`() {
      // given
      val model = MultiModuleProject("bamboo-tools", "fluid")
      val scaffold = scaffold {
        template("build.gradle", model, Source("templates/build.gradle"))
      }

      // when
      val commands = scaffold.transformDslToCommands()

      // then
      assertThat(commands)
        .containsExactly(
          TemplateCommand("build.gradle", model, Source("templates/build.gradle"))
        )
    }

    @Test
    fun `it should create template commands (nested directory) with source`() {
      // given
      val model = MultiModuleProject("bamboo-tools", "fluid")
      val scaffold = scaffold {
        dir("fluid") {
          template("build.gradle", model, Source("templates/build.gradle"))
        }
      }

      // when
      val commands = scaffold.transformDslToCommands()

      // then
      assertThat(commands)
        .containsExactly(
          DirectoryCommand("fluid"),
          TemplateCommand("fluid/build.gradle", model, Source("templates/build.gradle"))
        )
    }
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
    val commands = scaffold.transformDslToCommands()

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

  @Nested
  inner class ExecutablePermission {
    @Test
    fun `it should allow a file to have executable permission`() {
      // given
      val scaffold = scaffold {
        file("gradlew", EXECUTE)
      }

      // when
      val commands = scaffold.transformDslToCommands()

      // then
      assertThat(commands)
        .containsExactly(FileCommand("gradlew", MIRROR_DESTINATION, EXECUTE))
    }

    @Test
    fun `it should allow a file with source to have executable permission`() {
      // given
      val scaffold = scaffold {
        file("gradlew", Source("scripts/gradlew"), EXECUTE)
      }

      // when
      val commands = scaffold.transformDslToCommands()

      // then
      assertThat(commands)
        .containsExactly(FileCommand("gradlew", Source("scripts/gradlew"), EXECUTE))
    }
  }

  data class MultiModuleProject(
    val projectName: String,
    val moduleName: String
  )
}
