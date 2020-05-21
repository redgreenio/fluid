package io.redgreen.ask

import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle.DEFAULT
import org.jline.utils.AttributedStyle.GREEN
import org.jline.utils.Colors

private const val questionMark = "? "
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
  return buildPromptText(question)
    .apply { addDefaultValueToPromptIfPresent(question) }
    .toAnsi()
}

private fun buildPromptText(
  question: TextQuestion
): AttributedStringBuilder {
  return AttributedStringBuilder()
    .style(DEFAULT.foreground(GREEN))
    .append(questionMark)
    .style(DEFAULT)
    .append("${question.text} ")
}

private fun AttributedStringBuilder.addDefaultValueToPromptIfPresent(
  question: TextQuestion
) {
  if (question.default?.isBlank() == false) {
    style(DEFAULT.foreground(defaultValueTextColor))
    append("(${question.default}) ")
  }
}
