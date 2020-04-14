package io.redgreen.fluid.registry.assist

import io.redgreen.fluid.assist.ARTIFACT_VALID_GENERATOR
import io.redgreen.fluid.assist.getTestJarArtifact
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase
import io.redgreen.fluid.engine.domain.ValidateGeneratorJarUseCase.Result.ValidGenerator
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class ValidGeneratorParameterResolver : ParameterResolver {
  companion object {
    private val VALID_GENERATOR_JAR_PATH = getTestJarArtifact(ARTIFACT_VALID_GENERATOR)
  }

  override fun supportsParameter(
    parameterContext: ParameterContext,
    extensionContext: ExtensionContext
  ): Boolean =
    parameterContext.parameter.type == ValidGenerator::class.java

  override fun resolveParameter(
    parameterContext: ParameterContext,
    extensionContext: ExtensionContext
  ): Any =
    ValidateGeneratorJarUseCase().invoke(VALID_GENERATOR_JAR_PATH)
}
