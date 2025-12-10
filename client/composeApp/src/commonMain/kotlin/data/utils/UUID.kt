package data.utils

import java.util.UUID

fun createUUID(): String {
    return UUID.randomUUID().toString()
}