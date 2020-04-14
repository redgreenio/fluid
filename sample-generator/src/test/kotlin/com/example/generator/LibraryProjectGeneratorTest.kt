package com.example.generator

import io.redgreen.fluid.api.DirectoryEntry
import io.redgreen.fluid.api.FileEntry
import io.redgreen.fluid.testing.GeneratorSubject.Companion.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class LibraryProjectGeneratorTest {
  private val generator = LibraryProjectGenerator()

  @Test
  fun `it should generate a scaffold with files and directories`() {
    assertThat(generator)
      .generatesExactly(
        DirectoryEntry("src/main/java"),
        DirectoryEntry("src/main/kotlin"),
        DirectoryEntry("src/test/java"),
        DirectoryEntry("src/test/kotlin"),
        FileEntry("icon.png"),
        FileEntry(".gitignore"),
        FileEntry("build.gradle")
      )
  }

  @Test
  fun `it should copy a binary file`() {
    val strawberryBytes = this::class.java.classLoader
      .getResourceAsStream("strawberry.png")!!
      .readBytes()

    assertThat(generator)
      .generatesFileWithContent("icon.png", strawberryBytes)
  }

  @Test
  fun `it should copy a file`() {
    val content = """
      # Files
      local.properties
      *.iml

      # Directories
      /.gradle
      /build
      */build

      # IntelliJ Settings
      /.idea/*
      /.idea/caches
      /.idea/codeStyles
      /.idea/libraries
      !/.idea/runConfigurations
    """.trimIndent()

    assertThat(generator)
      .generatesFileWithContent(".gitignore", content)
  }

  @Test
  fun `it should copy a template`() {
    @Language("groovy")
    val content = """
      plugins {
        id 'java-library'
        id 'org.jetbrains.kotlin.jvm'
      }

      group 'com.mobsandgeeks'
      version '0.1.0-SNAPSHOT'
      
      repositories {
        mavenCentral()
      }

      dependencies {
        implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8'
      
        testImplementation 'org.junit.jupiter:junit-jupiter-api:5.6.0'
        testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.6.0'
        testImplementation 'com.google.truth:truth:1.0.1'
      }

      compileKotlin {
        kotlinOptions.jvmTarget = '1.8'
      }

      compileTestKotlin {
        kotlinOptions.jvmTarget = '1.8'
      }

      test {
        useJUnitPlatform()
      }
    """.trimIndent()

    assertThat(generator)
      .generatesFileWithContent("build.gradle", content)
  }
}
