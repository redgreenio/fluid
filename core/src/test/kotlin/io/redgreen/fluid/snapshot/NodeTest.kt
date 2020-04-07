package io.redgreen.fluid.snapshot

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class NodeTest {
  @Test
  fun `it should create a root node for an empty paths list`() {
    // given
    val noPaths = emptyList<String>()

    // when
    val tree = Node.buildPathsTree(noPaths)

    // then
    assertThat(tree)
      .isEqualTo(
        Node("/")
      )
  }

  @Test
  fun `it should create a tree with a single directory at root`() {
    // given
    val singleRootDirectory = listOf("src")

    // when
    val tree = Node.buildPathsTree(singleRootDirectory)

    // then
    assertThat(tree)
      .isEqualTo(Node("/", mutableListOf(Node("src"))))
  }

  @Test
  fun `it should create a tree with multiple directories at root`() {
    // given
    val multipleRootDirectories = listOf("src", "gradlew")

    // when
    val tree = Node.buildPathsTree(multipleRootDirectories)

    // then
    val rootDirectories = mutableListOf(Node("src"), Node("gradlew"))
    assertThat(tree)
      .isEqualTo(Node("/", rootDirectories))
  }

  @Test
  fun `it should create a tree with the longest path if paths have the same ancestors`() {
    // given
    val pathsWithCommonAncestor = listOf("src", "src/main", "src/main/kotlin")

    // when
    val tree = Node.buildPathsTree(pathsWithCommonAncestor)

    // then
    val kotlinNode = Node("kotlin")
    val mainNode = Node("main", mutableListOf(kotlinNode))
    val srcNode = Node("src", mutableListOf(mainNode))
    assertThat(tree)
      .isEqualTo(Node("/", mutableListOf(srcNode)))
  }

  @Test
  fun `it should return the root leaf path when the tree is empty`() {
    // given
    val tree = Node.buildPathsTree(emptyList())

    // when
    val leaves = tree.leaves()

    // then
    assertThat(leaves)
      .isEmpty()
  }

  @Test
  fun `it should return the root directory when the tree has one root directory`() {
    // given
    val tree = Node.buildPathsTree(listOf("src"))

    // when
    val leaves = tree.leaves()

    // then
    assertThat(leaves)
      .containsExactly("src")
  }

  @Test
  fun `it should return the leaf path when the path has the same ancestors`() {
    // given
    val tree = Node.buildPathsTree(listOf("src", "src/main", "src/main/kotlin"))

    // when
    val leaves = tree.leaves()

    // then
    assertThat(leaves)
      .containsExactly("src/main/kotlin")
  }

  @Test
  fun `it should return leaf paths with divergent nodes`() {
    // given
    val tree = Node.buildPathsTree(listOf("src", "src/main", "src/main/kotlin", "src/main/java"))

    // when
    val leaves = tree.leaves()

    // then
    assertThat(leaves)
      .containsExactly("src/main/kotlin", "src/main/java")
  }
}
