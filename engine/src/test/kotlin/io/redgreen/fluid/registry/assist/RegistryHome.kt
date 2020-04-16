package io.redgreen.fluid.registry.assist

import io.redgreen.fluid.registry.model.RegistryHome
import java.nio.file.Files
import java.nio.file.Path

fun RegistryHome.createRegistryFile(contents: String) {
  with(this.registryManifestPath) {
    if (!Files.exists(parent)) {
      Files.createDirectories(parent)
    }
    toFile().writeText(contents)
  }
}

fun RegistryHome.artifactPath(artifactFileName: String): Path {
  return this.artifactsPath.resolve(artifactFileName)
}
