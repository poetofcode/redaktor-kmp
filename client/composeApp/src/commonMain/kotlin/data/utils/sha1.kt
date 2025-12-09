package data.utils

import java.security.MessageDigest

/**
 * Encrypt String to SHA1 format
 */
fun String.toSha1(): String {
    return MessageDigest
        .getInstance("SHA-1")
        .digest(this.toByteArray())
        .joinToString(separator = "", transform = { "%02x".format(it) })
}