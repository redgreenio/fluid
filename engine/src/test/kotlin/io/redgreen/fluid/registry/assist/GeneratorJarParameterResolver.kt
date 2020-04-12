package io.redgreen.fluid.registry.assist

import io.redgreen.fluid.assist.ARTIFACT_VALID_GENERATOR
import io.redgreen.fluid.assist.getTestJarArtifact
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase
import io.redgreen.fluid.engine.domain.LoadGeneratorJarUseCase.Result.ValidGenerator
import io.redgreen.fluid.engine.model.GeneratorJar
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class GeneratorJarParameterResolver : ParameterResolver {
  companion object {
    private val VALID_GENERATOR_JAR_PATH = getTestJarArtifact(ARTIFACT_VALID_GENERATOR)
  }

  override fun supportsParameter(
    parameterContext: ParameterContext,
    extensionContext: ExtensionContext
  ): Boolean =
    parameterContext.parameter.type == GeneratorJar::class.java

  override fun resolveParameter(
    parameterContext: ParameterContext,
    extensionContext: ExtensionContext
  ): Any {
    return (LoadGeneratorJarUseCase()
      .invoke(VALID_GENERATOR_JAR_PATH) as ValidGenerator)
      .generatorJar()
  }
}
