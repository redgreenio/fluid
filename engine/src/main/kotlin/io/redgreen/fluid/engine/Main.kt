package io.redgreen.fluid.engine

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.snapshot.InMemorySnapshotFactory
import java.io.File
import java.net.URLClassLoader

fun main() {
  val generatorJarFileUrl = File("fluid/sample-generator/out/bin/generator.jar").toURI().toURL()
  val generatorClassLoader = URLClassLoader(arrayOf(generatorJarFileUrl))
  generatorClassLoader.loadClass("com.example.generator.LibraryProjectGenerator")
  val generatorClass = Class
    .forName("com.example.generator.LibraryProjectGenerator", false, generatorClassLoader) as Class<Generator>
  val generator = generatorClass.getDeclaredConstructor().newInstance() as Generator

  val snapshot = generator.scaffold().buildSnapshot(InMemorySnapshotFactory(), generatorClass)
  println(snapshot)
}
