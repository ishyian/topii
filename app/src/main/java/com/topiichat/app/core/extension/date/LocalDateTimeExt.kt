package com.topiichat.app.core.extension.date

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import org.threeten.bp.temporal.ChronoUnit
import timber.log.Timber
import java.util.Date
import java.util.Locale

fun Long.toDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this).atZone(ZONE_ID_UTC).toLocalDateTime()
}

fun LocalDateTime.toDate(): Date {
    return Date(toMillis())
}

fun LocalDateTime.toMillis(): Long {
    return atZone(ZONE_ID_UTC).toInstant().toEpochMilli()
}

fun LocalDateTime.toString(pattern: String): String {
    val formatter = when (pattern) {
        DateTimeFormats.FULL_DATE_TIME -> {
            DateTimeFormats.FULL_DATE_TIME_FORMATTER
        }
        DateTimeFormats.FULL_DISPLAYED_DATE_TIME -> {
            DateTimeFormats.FULL_DISPLAYED_DATE_TIME_FORMATTER
        }
        else -> {
            DateTimeFormatter.ofPattern(pattern)
        }
    }
    return toString(formatter)
}

fun LocalDateTime.toString(formatter: DateTimeFormatter): String {
    return format(formatter)
}

fun LocalDateTime.toZonedDateTime(): ZonedDateTime {
    val zoneId = ZoneId.systemDefault()
    return atZone(zoneId)
}

fun String.toLocalDateTime(pattern: String?): LocalDateTime {
    val formatter = when (pattern) {
        DateTimeFormats.FULL_DATE_TIME -> {
            DateTimeFormats.FULL_DATE_TIME_FORMATTER
        }
        else -> {
            DateTimeFormatter.ofPattern(pattern)
        }
    }
    return toLocalDateTime(formatter)
}

fun String.toLocalDateTime(formatter: DateTimeFormatter): LocalDateTime {
    return LocalDateTime.parse(this, formatter)
}

fun String.toLocalDateTimeOrNull(formatter: DateTimeFormatter): LocalDateTime? {
    return try {
        LocalDateTime.parse(this, formatter)
    } catch (e: DateTimeParseException) {
        Timber.e(e)
        null
    }
}

fun String.toZonedLocalDateTime(pattern: String): LocalDateTime {
    val formatter = when (pattern) {
        DateTimeFormats.FULL_DATE_TIME -> {
            DateTimeFormats.FULL_DATE_TIME_FORMATTER
        }
        else -> {
            DateTimeFormatter.ofPattern(pattern)
        }
    }

    return this.toZonedDateTime(formatter).toLocalLocalDateTime()
}

infix fun LocalDateTime.monthsLeftTo(localDateTime: LocalDateTime): Long = leftTo(localDateTime, ChronoUnit.MONTHS)

infix fun LocalDateTime.weeksLeftTo(localDateTime: LocalDateTime): Long = leftTo(localDateTime, ChronoUnit.WEEKS)

infix fun LocalDateTime.daysLeftTo(localDateTime: LocalDateTime): Long = leftTo(localDateTime, ChronoUnit.DAYS)

infix fun LocalDateTime.hoursLeftTo(localDateTime: LocalDateTime): Long = leftTo(localDateTime, ChronoUnit.HOURS)

infix fun LocalDateTime.minutesLeftTo(localDateTime: LocalDateTime): Long = leftTo(localDateTime, ChronoUnit.MINUTES)

infix fun LocalDateTime.secondsLeftTo(localDateTime: LocalDateTime): Long = leftTo(localDateTime, ChronoUnit.SECONDS)

/**
 * Округляет значение оставшегося времени в верхнюю сторону
 * Реальная разница Округленное значение
 * 1m 1d            2m
 * 8d               2w
 * 23h 1min         24h
 */
fun LocalDateTime.leftTo(localDateTime: LocalDateTime, unit: ChronoUnit): Long {
    return until(localDateTime, unit) + 1
}

object DateTimeFormats {
    const val FULL_DATE_TIME = "yyyy-MM-dd HH:mm"
    const val FULL_DISPLAYED_DATE_TIME = "d MMMM HH:mm"
    const val FORMAT_INCOMING = "yyyy-MM-dd HH:mm:ssZ"
    const val FORMAT_DAYS_MONTH_YEAR = "d MMMM yyyy"
    const val FULL_DISPLAYED_DATE_COMMA_TIME = "d MMMM',' HH:mm"
    const val FORMAT_MINUTES_SECONDS = "mm:ss"
    const val FORMAT_HOURS_MINUTES = "HH:mm"
    private const val FULL_DISPLAYED_DAY_MONTH = "d MMMM"
    val SERVER_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME
    val FULL_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(FULL_DATE_TIME)
    val FULL_DISPLAYED_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(FULL_DISPLAYED_DATE_TIME)
        .withLocale(Locale.US)
    val FULL_DISPLAYED_DAY_MONTH_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(FULL_DISPLAYED_DAY_MONTH)
}