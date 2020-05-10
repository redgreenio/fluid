package io.redgreen.ask

import org.jline.reader.LineReader
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle.DEFAULT
import org.jline.utils.AttributedStyle.GREEN
import org.jline.utils.Colors

class TextInput(
  private val what: String,
  private val defaultValue: String = ""
) {
  companion object {
    private const val QUESTION_MARK = "? "
    private val defaultValueTextColor = Colors.roundRgbColor(128, 128, 128, 255)
  }

  fun prompt(
    lineReader: LineReader
  ): String {
    val inputText = lineReader.readLine(buildAnsiText())
    return if (inputText.isBlank()) {
      defaultValue
    } else {
      inputText
    }
  }

  private fun buildAnsiText(): String {
    val promptTextBuilder = AttributedStringBuilder()
      .style(DEFAULT.foreground(GREEN))
      .append(QUESTION_MARK)
      .style(DEFAULT)
      .append("$what ")

    return promptTextBuilder
      .apply {
        if (!defaultValue.isBlank()) {
          style(DEFAULT.foreground(defaultValueTextColor))
          append("($defaultValue) ")
        }
      }
      .toAnsi()
  }
}
