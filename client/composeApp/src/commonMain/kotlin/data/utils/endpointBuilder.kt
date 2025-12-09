package data.utils

fun String.buildEndpoint(baseUrl: String): String {
    return "$baseUrl/$this"
        .replace("://", "{prefix}")
        .replace("//", "/")
        .replace("{prefix}", "://")
}