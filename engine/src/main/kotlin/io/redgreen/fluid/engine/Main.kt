package io.redgreen.fluid.engine

import io.redgreen.fluid.api.Generator
import io.redgreen.fluid.snapshot.InMemorySnapshotFactory
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarInputStream

fun main() {
  val jarPath = "fluid/sample-generator/out/bin/sample-generator-1.0-SNAPSHOT.jar"

  val generatorJarFileUrl = File(jarPath).toURI().toURL()
  val generatorClassLoader = URLClassLoader(arrayOf(generatorJarFileUrl))

  val manifest = JarInputStream(generatorJarFileUrl.openStream()).manifest
  val generatorClassName =  manifest.mainAttributes.getValue("Generator")

  generatorClassLoader.loadClass(generatorClassName)
  val generatorClass = Class
    .forName(generatorClassName, false, generatorClassLoader).asSubclass(Generator::class.java)

  val generator = generatorClass.getDeclaredConstructor().newInstance() as Generator

  val snapshot = generator.scaffold().buildSnapshot(InMemorySnapshotFactory(), generatorClass)
  println(snapshot)
}
