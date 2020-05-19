package io.redgreen.experiments

import io.redgreen.experiments.DateUtil.Tense
import io.redgreen.experiments.DateUtil.Tense.ago
import io.redgreen.experiments.DateUtil.Tense.from_now
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH

infix fun Int.days(timing: Tense): DateUtil =
  DateUtil(this, timing)

class DateUtil(val number: Int, val tense: Tense) {
  enum class Tense {
    ago, from_now
  }

  override fun toString(): String {
    val today = Calendar.getInstance()

    when(tense) {
      ago -> today.add(DAY_OF_MONTH, -number)
      from_now -> today.add(DAY_OF_MONTH, number)
    }
    return today.time.toString()
  }
}
