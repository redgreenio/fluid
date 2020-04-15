package io.redgreen.fluid.extensions

import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest

internal fun computeSha256(inputStream: InputStream): String {
  val messageDigest = MessageDigest.getInstance("SHA-256")
  inputStream.use { bufferedStream ->
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE) // https://stackoverflow.com/a/237495/421372
    DigestInputStream(bufferedStream, messageDigest).use { digestInputStream ->
      var readBytes: Int
      do {
        readBytes = digestInputStream.read(buffer)
      } while (readBytes != -1)
    }
  }
  return messageDigest
    .digest()
    .toHexString()
}

private fun ByteArray.toHexString(): String =
  this.joinToString("") { String.format("%02x", it) }
