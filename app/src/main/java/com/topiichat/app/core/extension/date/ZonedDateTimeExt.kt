package com.topiichat.app.core.extension.date

import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

fun ZonedDateTime.toString(pattern: String): String {
    val formatter = when (pattern) {
        ZonedDateTimeFormats.FULL_DATE_TIME_Z -> {
            ZonedDateTimeFormats.FULL_DATE_TIME_Z_FORMATTER
        }
        ZonedDateTimeFormats.FULL_DATE_TIME_WITH_EXTRA_TIMEZONE -> {
            ZonedDateTimeFormats.FULL_DATE_TIME_WITH_EXTRA_TIMEZONE_FORMATTER
        }
        else -> {
            DateTimeFormatter.ofPattern(pattern)
        }
    }
    return toString(formatter)
}

fun ZonedDateTime.toString(formatter: DateTimeFormatter): String {
    return format(formatter)
}

fun String.toZonedDateTime(pattern: String): ZonedDateTime {
    val formatter = when (pattern) {
        ZonedDateTimeFormats.FULL_DATE_TIME_Z -> {
            ZonedDateTimeFormats.FULL_DATE_TIME_Z_FORMATTER
        }
        ZonedDateTimeFormats.FULL_DATE_TIME_WITH_EXTRA_TIMEZONE -> {
            ZonedDateTimeFormats.FULL_DATE_TIME_WITH_EXTRA_TIMEZONE_FORMATTER
        }
        else -> {
            DateTimeFormatter.ofPattern(pattern)
        }
    }
    return toZonedDateTime(formatter)
}

fun String.toZonedDateTime(formatter: DateTimeFormatter): ZonedDateTime {
    return ZonedDateTime.parse(this, formatter)
}

fun String.toLocalZonedDateTime(formatter: DateTimeFormatter): LocalDateTime {
    return ZonedDateTime.of(this.toLocalDateTime(formatter), ZONE_ID_UTC)
        .toLocalLocalDateTime()
}

fun ZonedDateTime.toLocalLocalDateTime(): LocalDateTime {
    return withZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()
}

object ZonedDateTimeFormats {
    const val FULL_DATE_TIME_Z = "yyyy-MM-dd HH:mm:ssZ"
    const val FULL_DATE_TIME_WITH_EXTRA_TIMEZONE = "yyyy-MM-dd HH:mm:ssXXXX"
    val FULL_DATE_TIME_Z_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(FULL_DATE_TIME_Z)
    val FULL_DATE_TIME_WITH_EXTRA_TIMEZONE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(
        FULL_DATE_TIME_WITH_EXTRA_TIMEZONE
    )
}