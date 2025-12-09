package data.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object DateFormatter {

    private val humanDateFormat by lazy {
        "dd.MM.yyyy 'в' HH:mm"
    }

    private val locale by lazy {
        Locale.getDefault()
    }

    fun getHumanDate(date: String): String =
        runCatching {
            val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            val dateEdited = date.replace("Z", "-0000")
            var result1: Date? = df1.parse(dateEdited)
            SimpleDateFormat(humanDateFormat, locale).format(result1)
        }.getOrDefault(defaultValue = date)
}