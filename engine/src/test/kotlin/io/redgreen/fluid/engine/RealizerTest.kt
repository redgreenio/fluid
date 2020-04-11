package io.redgreen.fluid.engine

import com.google.common.truth.Truth.assertThat
import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.api.Snapshot
import io.redgreen.fluid.dsl.Scaffold
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
    val snapshot = scaffold {
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
    val snapshot = scaffold {
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
    val resourceBytes = this::class.java.classLoader.getResourceAsStream("images/strawberry.png")!!.readAllBytes()
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

  private fun Scaffold.snapshot(): Snapshot {
    val generator = ShellScaffoldGenerator(this)
    return generator
      .scaffold()
      .buildSnapshot(InMemorySnapshotFactory(), generator::class.java.asSubclass(Generator::class.java))
  }
}
