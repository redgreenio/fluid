package io.redgreen.fluid.assist

import java.io.File

private const val PATH_JAR_TEST_ARTIFACTS = "src/test/resources/jar-test-artifacts"

internal const val ARTIFACT_VALID_GENERATOR: String = "valid-generator.jar"

internal fun getTestJarArtifact(artifactName: String): String =
  File("")
    .resolve(PATH_JAR_TEST_ARTIFACTS)
    .resolve(artifactName)
    .absolutePath
