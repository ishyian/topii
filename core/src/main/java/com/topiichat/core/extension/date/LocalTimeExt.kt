package pyaterochka.app.base.util.threeten.bp

import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

fun LocalTime.toString(pattern: String): String {
    return toString(DateTimeFormatter.ofPattern(pattern))
}

fun LocalTime.toString(formatter: DateTimeFormatter): String {
    return format(formatter)
}

fun String.toLocalTime(pattern: String): LocalTime {
    return toLocalTime(DateTimeFormatter.ofPattern(pattern))
}

fun String.toLocalTime(formatter: DateTimeFormatter): LocalTime {
    return LocalTime.parse(this, formatter)
}