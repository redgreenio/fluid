package io.redgreen.fluid.snapshot

import java.util.Stack

typealias Segment = String

data class Node(
  val name: String,
  val children: MutableList<Node> = mutableListOf()
) {
  companion object {
    /* The choice of path separator is intentional because the in-memory file system uses Unix configuration. */
    private const val PATH_SEPARATOR = "/"
    private const val ROOT = "/"

    fun buildPathsTree(paths: List<String>): Node {
      paths.ifEmpty { return Node(ROOT) }

      return Node(ROOT, mutableListOf()).also { tree ->
        paths
          .map { it.split(PATH_SEPARATOR) }
          .onEach { segments -> push(tree, segments) }
      }
    }

    private tailrec fun push(node: Node, segments: List<Segment>) {
      if (segments.isEmpty()) return

      val nodeName = segments.first()
      val childrenNodeNames = node.children.map { it.name }

      val existingChild = childrenNodeNames.contains(nodeName)
      val currentNode = if (existingChild) {
        val childNodeIndex = childrenNodeNames.indexOf(nodeName)
        node.children[childNodeIndex]
      } else {
        Node(nodeName).also { newNode -> node.children.add(newNode) }
      }

      push(currentNode, segments.drop(1))
    }
  }

  fun leaves(): List<String> {
    return mutableListOf<String>().also { pathCollector ->
      getLeafPaths(this, Stack(), pathCollector)
    }.toList()
  }

  private fun getLeafPaths(
    node: Node,
    segments: Stack<Segment>,
    pathCollector: MutableList<String>
  ) {
    segments.push(node.name)

    if (node.isLeaf()) {
      val leafPath = constructPath(segments)
      if (leafPath.isNotEmpty()) {
        pathCollector.add(leafPath)
      }
      segments.pop()
    } else {
      for (child in node.children) {
        getLeafPaths(child, segments, pathCollector)
      }
      segments.pop()
    }
  }

  private fun constructPath(segments: Stack<Segment>): String =
    segments.elements().toList()
      .drop(1) // Drops the leading '/' character
      .joinToString(PATH_SEPARATOR)

  private fun isLeaf(): Boolean =
    children.isEmpty()
}
