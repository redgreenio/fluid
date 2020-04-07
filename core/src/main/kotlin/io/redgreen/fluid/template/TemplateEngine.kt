package io.redgreen.fluid.template

interface TemplateEngine {
  fun <T : Any> processTemplate(templatePath: String, model: T): String
}
