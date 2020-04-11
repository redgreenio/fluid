package io.redgreen.fluid.template

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import java.io.StringWriter

class FreemarkerTemplateEngine(
  classLoaderClass: Class<*>
) : TemplateEngine {
  companion object {
    private const val UTF_8 = "UTF-8"
    private const val KEY_MODEL = "model"
    private const val ROOT_BASE_PACKAGE_PATH = ""
  }

  private val classLoader = classLoaderClass.classLoader

  override fun <T : Any> processTemplate(templatePath: String, model: T): String {
    val configuration = getConfiguration()

    val dataModel = mapOf(KEY_MODEL to model)

    val template = configuration.getTemplate(templatePath)
    val writer = StringWriter()
    template.process(dataModel, writer)

    return writer.toString()
  }

  private fun getConfiguration(): Configuration {
    return Configuration(Configuration.VERSION_2_3_30).apply {
      defaultEncoding = UTF_8
      templateLoader = ClassTemplateLoader(classLoader, ROOT_BASE_PACKAGE_PATH)
    }
  }
}
