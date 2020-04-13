package io.redgreen.fluid.registry.assist

import io.redgreen.fluid.registry.model.RegistryHome
import java.nio.file.Files

fun RegistryHome.createRegistryFile(contents: String) {
  with(this.registryFilePath) {
    if (!Files.exists(parent)) {
      Files.createDirectories(parent)
    }
    toFile().writeText(contents)
  }
}
