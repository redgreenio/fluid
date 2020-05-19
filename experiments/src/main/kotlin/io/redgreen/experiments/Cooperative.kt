package io.redgreen.experiments

import kotlinx.coroutines.yield

suspend fun task1() {
  println("Start task 1")
  yield()
  println("End task 1")
}

suspend fun task2() {
  println("Start task 2")
  yield()
  println("End task 2")
}

val sequence = sequence {
  val start = 0
  yield(start)
  yieldAll(1..5 step 2)
  // yieldAll(generateSequence(8) { it * 3 })
}

val fibonacci = sequence {
  var terms = Pair(0, 1)

  while (true) {
    yield(terms.first)
    terms = Pair(terms.second, terms.first + terms.second)
  }
}

fun main() {
  println(sequence.toList())

  /*
  runBlocking {
    launch { task1() }
    launch { task2() }
  }
  */
}
