package io.redgreen.fluid.cli.internal

import picocli.CommandLine.IVersionProvider

class FluidVersionProvider : IVersionProvider {
  companion object {
    // See the 'Implementation-Version' attribute (jar.manifest.attributes) in the module's build.gradle file.
    private const val INDEX_VERSION = 0
    private const val INDEX_COMMIT_HASH = 1
    private const val INDEX_TIMESTAMP = 2
  }

  override fun getVersion(): Array<String> {
    // FIXME(rj) 5/May/20 - We have a trouble reading the manifest file for this jar. As a work-around, we are currently
    // FIXME relying on the `implementationVersion` property to cram build information.
    val buildInfo = FluidVersionProvider::class.java.`package`
      .implementationVersion
      .split("|")

    return arrayOf(
      version(buildInfo[INDEX_VERSION]),
      buildHash(buildInfo[INDEX_COMMIT_HASH]),
      buildTimestamp(buildInfo[INDEX_TIMESTAMP])
    )
  }

  private fun version(name: String): String =
    "Version: $name"

  private fun buildHash(commitHash: String): String {
    val shortHash = commitHash.take(10)
    return "Build: $shortHash"
  }

  private fun buildTimestamp(timestamp: String): String {
    return "Build-Timestamp: $timestamp"
  }
}
