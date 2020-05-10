package io.redgreen.fluid.engine

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.dsl.Permission.EXECUTE
import io.redgreen.fluid.dsl.Scaffold
import io.redgreen.fluid.dsl.Source.Companion.MIRROR_DESTINATION
import io.redgreen.fluid.dsl.scaffold
import io.redgreen.fluid.engine.assist.ShellScaffoldGenerator
import io.redgreen.fluid.engine.model.DirectoryCreated
import io.redgreen.fluid.engine.model.FileCreated
import io.redgreen.fluid.snapshot.InMemorySnapshotFactory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class RealizerTest {
  @TempDir
  lateinit var destinationRoot: File

  private val realizer = Realizer()

  @Test
  fun `it should realize directories on the file system`() {
    // given
    val directoryName = "hello-world"
    val snapshot = scaffold<Unit> {
      dir(directoryName)
    }.snapshot()

    // when
    val realizations = realizer.realize(destinationRoot, snapshot)

    // then
    val realizedDirectory = destinationRoot.resolve(directoryName)
    assertThat(realizedDirectory.exists())
      .isTrue()

    assertThat(realizations)
      .containsExactly(
        DirectoryCreated(directoryName)
      )
  }

  @Test
  fun `it should realize files on the file system`() {
    // given
    val snapshot = scaffold<Unit> {
      dir("images") {
        file("strawberry.png")
      }
    }.snapshot()

    // when
    val realizations = realizer.realize(destinationRoot, snapshot)

    // then - file exists
    val realizedFile = destinationRoot.resolve("images/strawberry.png")
    assertThat(realizedFile.exists())
      .isTrue()

    // then - contents are the same
    val resourceBytes = this::class.java.classLoader
      .getResourceAsStream("images/strawberry.png")!!
      .readBytes()
    val realizedBytes = realizedFile.readBytes()
    assertThat(resourceBytes)
      .isEqualTo(realizedBytes)

    // then - produces realization
    assertThat(realizations)
      .containsExactly(
        FileCreated("images/strawberry.png"),
        DirectoryCreated("images")
      )
      .inOrder()
  }

  @Test
  fun `it should realize file with executable permission on the file system`() {
    // given
    val snapshot = scaffold<Unit> {
      template("greet", "Kumar", MIRROR_DESTINATION, EXECUTE)
    }.snapshot()

    // when
    realizer.realize(destinationRoot, snapshot)

    // then
    val greetFilePath = destinationRoot.resolve("greet")
    assertThat(greetFilePath.readText())
      .isEqualTo(
        """
          echo "Hello, Kumar"
        """.trimIndent()
      )
    assertThat(greetFilePath.canExecute())
      .isTrue()
  }

  private fun Scaffold<Unit>.snapshot(): Snapshot {
    val generator = ShellScaffoldGenerator(this)
    return generator
      .scaffold()
      .buildSnapshot(InMemorySnapshotFactory(), generator::class.java.asSubclass(Generator::class.java))
  }
}
