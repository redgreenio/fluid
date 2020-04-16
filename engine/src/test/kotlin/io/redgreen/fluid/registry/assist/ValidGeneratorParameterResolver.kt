package io.redgreen.fluid.registry.assist

import io.redgreen.fluid.assist.getTestArtifact
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase
import io.redgreen.fluid.engine.domain.ValidateGeneratorUseCase.Result.ValidGenerator
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

class ValidGeneratorParameterResolver : ParameterResolver {
  override fun supportsParameter(
    parameterContext: ParameterContext,
    extensionContext: ExtensionContext
  ): Boolean =
    parameterContext.parameter.type == ValidGenerator::class.java

  override fun resolveParameter(
    parameterContext: ParameterContext,
    extensionContext: ExtensionContext
  ): Any {
    val artifact = parameterContext.parameter.getAnnotation(TestArtifact::class.java)
    return ValidateGeneratorUseCase().invoke(getTestArtifact(artifact.name))
  }

  annotation class TestArtifact(
    val name: String
  )
}
