package io.redgreen.fluid.template

interface TemplateEngine {
  fun <T> processTemplate(templatePath: String, model: T): String
}
