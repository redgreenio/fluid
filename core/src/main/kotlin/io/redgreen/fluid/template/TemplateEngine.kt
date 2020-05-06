package io.redgreen.fluid.template

interface TemplateEngine {
  fun <M : Any> processTemplate(
    templatePath: String,
    model: M
  ): String
}
