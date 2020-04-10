package com.example.generator

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.dsl.Resource
import io.redgreen.fluid.dsl.Scaffold
import io.redgreen.fluid.dsl.scaffold

class LibraryProjectGenerator : Generator {
  override fun scaffold(): Scaffold {
    val config = LibraryProjectConfig("com.mobsandgeeks", "0.1.0-SNAPSHOT")

    return scaffold {
      dir("src") {
        val sourceSets = listOf("main", "test")
        sourceSets.onEach { sourceSet ->
          dir(sourceSet) {
            dir("java")
            dir("kotlin")
          }
        }
      }

      file("icon.png", Resource("strawberry.png"))
      file(".gitignore", Resource("gitignore"))
      template("build.gradle", config)
    }
  }
}
