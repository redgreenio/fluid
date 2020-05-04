package io.redgreen.fluid.registry.model

import com.github.zafarkhaja.semver.UnexpectedCharacterException
import com.github.zafarkhaja.semver.Version

enum class VersionComparison {
  NEWER, OLDER, EQUAL, NA;

  companion object {
    fun compareCandidate(
      installed: String,
      candidate: String
    ): VersionComparison {
      return try {
        val comparisonResult = Version.valueOf(installed)
          .compareTo(Version.valueOf(candidate))
        return mapComparisionResult(comparisonResult)
      } catch (e: UnexpectedCharacterException) {
        e.printStackTrace()
        NA
      }
    }

    private fun mapComparisionResult(
      comparisonResult: Int
    ): VersionComparison {
      return when {
        comparisonResult == 0 -> EQUAL
        comparisonResult > 0 -> OLDER
        else -> NEWER
      }
    }
  }
}
