package io.redgreen.fluid

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.DirectoryCommand
import io.redgreen.fluid.api.FileCommand
import io.redgreen.fluid.api.TemplateCommand
import io.redgreen.fluid.assist.ScaffoldSubject.Companion.assertThat
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
    val scaffold = scaffold<Unit> {
      /* empty */
    }

    // when
    val exception = assertThrows<IllegalStateException> {
      scaffold.transformDslToCommands(Unit)
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
      val scaffold = scaffold<Unit> {
        dir("src")
      }

      assertThat(scaffold)
        .produces(
          DirectoryCommand("src")
        )
    }

    @Test
    fun `it should return directory commands for nested directories`() {
      val scaffold = scaffold<Unit> {
        dir("src") {
          dir("main")
        }
      }

      assertThat(scaffold)
        .produces(
          DirectoryCommand("src"),
          DirectoryCommand("src/main")
        )
    }
  }

  @Nested
  inner class File {
    @Test
    fun `it should return a file copy command for a file copy call`() {
      val scaffold = scaffold<Unit> {
        file(".gitignore")
      }

      assertThat(scaffold)
        .produces(
          FileCommand(".gitignore")
        )
    }

    @Test
    fun `it should return a file copy command for files nested inside directories`() {
      val scaffold = scaffold<Unit> {
        dir("src/test") {
          file("CanaryTest.kt")
        }
      }

      assertThat(scaffold)
        .produces(
          DirectoryCommand("src/test"),
          FileCommand("src/test/CanaryTest.kt")
        )
    }
  }

  @Nested
  inner class Template {
    @Test
    fun `it should create a template command`() {
      val model = MultiModuleProject("bamboo-tools", "fluid")
      val scaffold = scaffold<Unit> {
        template("settings.gradle", model)
      }

      assertThat(scaffold)
        .produces(
          TemplateCommand("settings.gradle", model)
        )
    }

    @Test
    fun `it should create template commands inside nested directories`() {
      val model = MultiModuleProject("bamboo-tools", "fluid")
      val scaffold = scaffold<Unit> {
        dir("fluid") {
          template("fluid.iml", model)
        }
      }

      assertThat(scaffold)
        .produces(
          DirectoryCommand("fluid"),
          TemplateCommand("fluid/fluid.iml", model)
        )
    }
  }

  @Nested
  inner class ExplicitSourceInFile {
    @Test
    fun `it should allow specifying source path while copying files`() {
      val scaffold = scaffold<Unit> {
        file(".gitignore", Source("gitignore"))
      }

      assertThat(scaffold)
        .produces(
          FileCommand(".gitignore", Source("gitignore"))
        )
    }

    @Test
    fun `it should allow specifying source path while copying files into directories`() {
      val scaffold = scaffold<Unit> {
        dir("core") {
          file(".gitignore", Source("gitignore"))
        }
      }

      assertThat(scaffold)
        .produces(
          DirectoryCommand("core"),
          FileCommand("core/.gitignore", Source("gitignore"))
        )
    }
  }

  @Nested
  inner class ExplicitSourceInTemplate {
    @Test
    fun `it should create template commands (root) with source`() {
      val model = MultiModuleProject("bamboo-tools", "fluid")
      val scaffold = scaffold<Unit> {
        template("build.gradle", model, Source("templates/build.gradle"))
      }

      assertThat(scaffold)
        .produces(
          TemplateCommand("build.gradle", model, Source("templates/build.gradle"))
        )
    }

    @Test
    fun `it should create template commands (nested directory) with source`() {
      val model = MultiModuleProject("bamboo-tools", "fluid")
      val scaffold = scaffold<Unit> {
        dir("fluid") {
          template("build.gradle", model, Source("templates/build.gradle"))
        }
      }

      assertThat(scaffold)
        .produces(
          DirectoryCommand("fluid"),
          TemplateCommand("fluid/build.gradle", model, Source("templates/build.gradle"))
        )
    }
  }

  @Test
  fun `it should work with all kinds of elements`() {
    val scaffold = scaffold<Unit> {
      dir("one-directory")

      dir("directory") {
        dir("directory")
        file("file")

        dir("templates") {
          template("rocker.html", "Fluid")
        }
      }
    }

    assertThat(scaffold)
      .produces(
        DirectoryCommand("one-directory"),
        DirectoryCommand("directory"),
        DirectoryCommand("directory/directory"),
        FileCommand("directory/file"),
        DirectoryCommand("directory/templates"),
        TemplateCommand("directory/templates/rocker.html", "Fluid")
      )
  }

  @Nested
  inner class TopLevelExecutePermission {
    @Test
    fun `it should allow a file to have execute permission`() {
      val scaffold = scaffold<Unit> {
        file("gradlew", EXECUTE)
      }

      assertThat(scaffold)
        .produces(
          FileCommand("gradlew", MIRROR_DESTINATION, EXECUTE)
        )
    }

    @Test
    fun `it should allow a file with source to have execute permission`() {
      val scaffold = scaffold<Unit> {
        file("gradlew", Source("scripts/gradlew"), EXECUTE)
      }

      assertThat(scaffold)
        .produces(
          FileCommand("gradlew", Source("scripts/gradlew"), EXECUTE)
        )
    }

    @Test
    fun `it should allow a template to have execute permission`() {
      val scaffold = scaffold<Unit> {
        template("start-server", 8080, EXECUTE)
      }

      assertThat(scaffold)
        .produces(
          TemplateCommand("start-server", 8080, MIRROR_DESTINATION, EXECUTE)
        )
    }

    @Test
    fun `it should allow a template with source to have execute permission`() {
      val scaffold = scaffold<Unit> {
        template("start-server", 8080, Source("scripts/gradlew"), EXECUTE)
      }

      assertThat(scaffold)
        .produces(
          TemplateCommand("start-server", 8080, Source("scripts/gradlew"), EXECUTE)
        )
    }
  }

  @Nested
  inner class NestedExecutePermission {
    @Test
    fun `it should allow a nested file to have execute permission`() {
      val scaffold = scaffold<Unit> {
        dir("scripts") {
          file("gradlew", EXECUTE)
        }
      }

      assertThat(scaffold)
        .produces(
          DirectoryCommand("scripts"),
          FileCommand("scripts/gradlew", MIRROR_DESTINATION, EXECUTE)
        )
    }

    @Test
    fun `it should allow nested file with source to have execute permission`() {
      val scaffold = scaffold<Unit> {
        dir("scripts") {
          file("gradlew", Source("build-scripts/gradlew"), EXECUTE)
        }
      }

      assertThat(scaffold)
        .produces(
          DirectoryCommand("scripts"),
          FileCommand("scripts/gradlew", Source("build-scripts/gradlew"), EXECUTE)
        )
    }

    @Test
    fun `it should allow a nested template to have execute permission`() {
      val scaffold = scaffold<Unit> {
        dir("scripts") {
          template("start-server", 8080, EXECUTE)
        }
      }

      assertThat(scaffold)
        .produces(
          DirectoryCommand("scripts"),
          TemplateCommand("scripts/start-server", 8080, MIRROR_DESTINATION, EXECUTE)
        )
    }

    @Test
    fun `it should allow a nested template with source to have execute permission`() {
      val scaffold = scaffold<Unit> {
        dir("scripts") {
          template("start-server", 8080, Source("build-scripts/gradlew"), EXECUTE)
        }
      }

      assertThat(scaffold)
        .produces(
          DirectoryCommand("scripts"),
          TemplateCommand("scripts/start-server", 8080, Source("build-scripts/gradlew"), EXECUTE)
        )
    }
  }

  data class MultiModuleProject(
    val projectName: String,
    val moduleName: String
  )
}
