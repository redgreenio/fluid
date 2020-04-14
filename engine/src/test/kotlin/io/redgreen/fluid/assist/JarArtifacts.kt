package io.redgreen.fluid.assist

import java.io.File
import java.nio.file.Path

private const val PATH_JAR_TEST_ARTIFACTS = "src/test/resources/jar-test-artifacts"

internal const val ARTIFACT_VALID_GENERATOR: String = "valid-generator.jar"

internal fun getTestArtifact(artifactName: String): Path =
  File("")
    .resolve(PATH_JAR_TEST_ARTIFACTS)
    .resolve(artifactName)
    .toPath()
