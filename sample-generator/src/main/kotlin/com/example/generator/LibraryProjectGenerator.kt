package com.example.generator

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.dsl.Scaffold
import io.redgreen.fluid.dsl.Source
import io.redgreen.fluid.dsl.scaffold

class LibraryProjectGenerator : Generator<LibraryProjectConfig> {
  override fun configure(): LibraryProjectConfig =
    LibraryProjectConfig("com.mobsandgeeks", "0.1.0-SNAPSHOT")

  override fun scaffold(): Scaffold<LibraryProjectConfig> {
    return scaffold { dslConfig ->
      dir("src") {
        val sourceSets = listOf("main", "test")
        sourceSets.onEach { sourceSet ->
          dir(sourceSet) {
            dir("java")
            dir("kotlin")
          }
        }
      }

      file("icon.png", Source("strawberry.png"))
      file(".gitignore", Source("gitignore"))
      template("build.gradle", dslConfig)
    }
  }
}
