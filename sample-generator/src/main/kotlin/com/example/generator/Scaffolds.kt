package com.example.generator

import io.redgreen.fluid.dsl.scaffold

internal val gradleJavaKotlinLibraryScaffold = scaffold {
  val sourceSets = listOf("main", "test")

  sourceSets.onEach { sourceSet ->
    dir("src") {
      dir(sourceSet) {
        dir("java")
        dir("kotlin")
      }
    }

    file(".gitignore")
    template("build.gradle", JavaKotlinLibraryConfig("com.mobsandgeeks", "0.1.0-SNAPSHOT"))
  }
}

data class JavaKotlinLibraryConfig(
  val groupId: String,
  val version: String
)
