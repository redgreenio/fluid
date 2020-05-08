package io.redgreen.fluid.dsl

/**
 * Table from - https://www.linode.com/docs/tools-reference/linux-users-and-groups/#chmod-octal-format
 * ╔═════════════╤══════╤═══════╤═════════╗
 * ║ Octal Value │ Read │ Write │ Execute ║
 * ╠═════════════╪══════╪═══════╪═════════╣
 * ║      7      │  R   │   W   │    X    ║
 * ╟─────────────┼──────┼───────┼─────────╢
 * ║      6      │  R   │   W   │    -    ║
 * ╟─────────────┼──────┼───────┼─────────╢
 * ║      5      │  R   │   -   │    -    ║
 * ╟─────────────┼──────┼───────┼─────────╢
 * ║      4      │  R   │   -   │    -    ║
 * ╟─────────────┼──────┼───────┼─────────╢
 * ║      3      │  -   │   -   │    -    ║
 * ╟─────────────┼──────┼───────┼─────────╢
 * ║      2      │  -   │   -   │    -    ║
 * ╟─────────────┼──────┼───────┼─────────╢
 * ║      1      │  -   │   -   │    -    ║
 * ╚═════════════╧══════╧═══════╧═════════╝
 */
object Permission {
  private const val R = 4
  private const val W = 2
  private const val X = 1

  const val READ_WRITE = R or W
  const val EXECUTE = R or W or X
}
