package io.redgreen.fluid.prompts

import io.redgreen.ask.TextQuestion
import io.redgreen.ask.ask
import io.redgreen.ask.textPrompt

fun stringInput(
  question: TextQuestion
): String =
  ask(question, ::textPrompt).value
