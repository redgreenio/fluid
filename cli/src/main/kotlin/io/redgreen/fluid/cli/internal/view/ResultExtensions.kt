package io.redgreen.fluid.cli.internal.view

import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.Result.FreshInstallSuccessful
import io.redgreen.fluid.engine.domain.InstallGeneratorUseCase.Result.OverwriteSuccessful
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.AlreadyInstalled
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentHashes
import io.redgreen.fluid.registry.domain.LookupGeneratorUseCase.Result.DifferentVersions
import io.redgreen.fluid.registry.model.VersionComparison
import io.redgreen.fluid.registry.model.VersionComparison.NA
import io.redgreen.fluid.registry.model.VersionComparison.NEWER
import io.redgreen.fluid.registry.model.VersionComparison.OLDER

fun FreshInstallSuccessful.userMessage(
  hash: String
): String {
  return """
      Digest: sha256:$hash
      Installed generator: '${this.registryEntry.id}'
    """.trimIndent()
}

fun AlreadyInstalled.userMessage(
  generatorId: String
): String {
  return """
    Generator '$generatorId' is already installed.
    No changes were made.
  """.trimIndent()
}

fun DifferentHashes.userMessage(
  generatorId: String,
  version: String
): String {
  return """
      Generator '$generatorId', version '$version' is already installed.
      However, the generator you are trying to install has a different hash.
      Installed sha256: ${this.installed}
      Candidate sha256: ${this.candidate}
    """.trimIndent()
}

fun DifferentVersions.userMessage(generatorId: String): String {
  val consequence = when (VersionComparison.compareCandidate(installed, candidate)) {
    NEWER -> "upgrade"
    OLDER -> "downgrade"
    NA -> "change"
    else -> throw UnsupportedOperationException("Unknown consequence for $this.")
  }.toUpperCase()
  return """
      Generator '$generatorId', version '$installed' is installed.
      The generator you are trying to install will $consequence it to '$candidate'.
    """.trimIndent()
}

fun OverwriteSuccessful.userMessage(
  hash: String
): String {
  return """
      Digest: sha256:$hash
      Generator overwritten: '${this.registryEntry.id}'
    """.trimIndent()
}
