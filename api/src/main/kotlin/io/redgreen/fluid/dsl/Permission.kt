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
  const val EXECUTE = 1

  private const val READ = 4
  private const val WRITE = 2
  internal const val READ_WRITE = READ or WRITE
}
