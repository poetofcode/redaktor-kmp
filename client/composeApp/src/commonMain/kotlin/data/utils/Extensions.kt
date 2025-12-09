package data.utils

import androidx.compose.ui.graphics.Color
import java.util.regex.Pattern

// https://stackoverflow.com/questions/60247480/color-from-hex-string-in-jetpack-compose
//val hashColorString = "#00AB18"
fun String.parseComposeColor() = Color(removePrefix("#").toLong(16) or 0x00000000FF000000)

fun String.isValidEmail(): Boolean {
    val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    return EMAIL_ADDRESS_PATTERN.matcher(this).matches()
}