package io.redgreen.fluid.registry.assist

import io.redgreen.fluid.registry.Registry
import java.nio.file.Files
import java.nio.file.Path

fun Registry.createRegistryFile(
  registryManifestJson: String
) {
  with(this.registryManifestPath) {
    if (!Files.exists(parent)) {
      Files.createDirectories(parent)
    }
    toFile().writeText(registryManifestJson)
  }
}

fun Registry.artifactPath(
  artifactFileName: String
): Path =
  this.artifactsPath.resolve(artifactFileName)
