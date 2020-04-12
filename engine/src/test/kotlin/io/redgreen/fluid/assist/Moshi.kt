package io.redgreen.fluid.assist

import com.squareup.moshi.Moshi

internal val moshi by lazy {
  Moshi
    .Builder()
    .build()
}
