package io.redgreen.fluid.dsl

/**
 * The class faintly attempts to model the POSIX permissions but not entirely. [EXECUTE] is the only
 * permission that is handled explicitly through the Java file APIs, whereas [READ_WRITE] remains as
 * a sane default value and a placeholder. It is not used and has no effect.
 *
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
  internal const val R = 4
  internal const val W = 2
  internal const val X = 1

  /**
   * Read/Write POSIX permission. The implementation does not handle this permission explicitly. This value is
   * used as a sane default value or placeholder.
   */
  const val READ_WRITE = R or W

  /**
   * Maybe a misnomer, this constant is a combination of Read/Write/Execute POSIX permissions. Only the
   * execute permission is respected through the Java file APIs. Other flags are meant for correctness, but
   * not interpreted.
   */
  const val EXECUTE = R or W or X
}
