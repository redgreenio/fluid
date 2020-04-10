package io.redgreen.fluid.template

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import java.io.StringWriter

class FreemarkerTemplateEngine(
  private val classLoaderClass: Class<*>
) : TemplateEngine {
  companion object {
    private const val UTF_8 = "UTF-8"
    private const val KEY_MODEL = "model"
    private const val ROOT_BASE_PACKAGE_PATH = ""
  }

  override fun <T : Any> processTemplate(templatePath: String, model: T): String {
    val configuration = getConfiguration(classLoaderClass)

    val dataModel = mapOf(KEY_MODEL to model)

    val template = configuration.getTemplate(templatePath)
    val writer = StringWriter()
    template.process(dataModel, writer)

    return writer.toString()
  }

  private fun getConfiguration(classLoaderClass: Class<*>): Configuration {
    return Configuration(Configuration.VERSION_2_3_30).apply {
      defaultEncoding = UTF_8
      templateLoader = ClassTemplateLoader(classLoaderClass.classLoader, ROOT_BASE_PACKAGE_PATH)
    }
  }
}
