package com.example.generator

import io.redgreen.fluid.dsl.Resource
import io.redgreen.fluid.dsl.scaffold

internal val libraryProjectScaffold = scaffold {
  dir("src") {
    val sourceSets = listOf("main", "test")
    sourceSets.onEach { sourceSet ->
      dir(sourceSet) {
        dir("java")
        dir("kotlin")
      }
    }
  }

  file(".gitignore", Resource("gitignore"))
  template("build.gradle", LibraryProjectConfig("com.mobsandgeeks", "0.1.0-SNAPSHOT"))
}

data class LibraryProjectConfig(
  val groupId: String,
  val version: String
)
