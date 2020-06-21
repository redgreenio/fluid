package io.redgreen.ask

import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle.DEFAULT
import org.jline.utils.AttributedStyle.GREEN
import org.jline.utils.Colors

private const val QUESTION_MARK = "? "
private const val COLON = ": "

@Suppress("MagicNumber")
private val defaultValueTextColor = Colors.roundRgbColor(128, 128, 128, 255)
private val terminal: Terminal = TerminalBuilder.builder().jansi(true).build()

fun textPrompt(
  question: TextQuestion
): String =
  lineReader().readLine(buildAnsiText(question))

private fun lineReader(): LineReader =
  LineReaderBuilder
    .builder()
    .terminal(terminal)
    .build()

private fun buildAnsiText(
  question: TextQuestion
): String {
  return buildPrompt(question.text)
    .apply { addDefaultValueToPrompt(question) }
    .toAnsi()
}

private fun buildPrompt(
  text: String
): AttributedStringBuilder {
  return AttributedStringBuilder()
    .style(DEFAULT.foreground(GREEN))
    .append(QUESTION_MARK)
    .style(DEFAULT)
    .append(text)
}

private fun AttributedStringBuilder.addDefaultValueToPrompt(
  question: TextQuestion
) {
  val hasDefaultValue = question.default?.isBlank() == false
  if (hasDefaultValue) {
    style(DEFAULT.foreground(defaultValueTextColor))
    append(" (${question.default}) ")
  } else {
    append(COLON)
  }
}
