package com.topiichat.core.extension.date

import com.topiichat.core.extension.date.DateFormats.FORMAT_MONTH
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.util.Locale

fun LocalDate.toString(pattern: String): String {
    val formatter = when (pattern) {
        DateFormats.DOT_DAY_FORMAT -> {
            DateFormats.DOT_DAY_FORMATTER
        }
        else -> {
            DateTimeFormatter.ofPattern(pattern)
        }
    }
    return toString(formatter)
}

fun LocalDate.toString(formatter: DateTimeFormatter): String {
    return format(formatter)
}

fun String.toLocalDate(pattern: String): LocalDate {
    val formatter = when (pattern) {
        DateFormats.DOT_DAY_FORMAT -> {
            DateFormats.DOT_DAY_FORMATTER
        }
        else -> {
            DateTimeFormatter.ofPattern(pattern)
        }
    }
    return toLocalDate(formatter)
}

fun String.toLocalDate(formatter: DateTimeFormatter): LocalDate {
    return LocalDate.parse(this, formatter)
}

fun LocalDate.getMonthName(): String {
    return month.getMonthName()
}

infix fun LocalDate.daysLeftTo(localDateTime: LocalDate): Long = leftTo(localDateTime, ChronoUnit.DAYS)

fun LocalDate.leftTo(localDateTime: LocalDate, unit: ChronoUnit): Long {
    return until(localDateTime, unit) + 1
}

fun LocalDate.getMonthNameEn(): String {
    val lastDayOfMonth = this.atStartOfDay()
        .withDayOfMonth(1)
        .plusMonths(1)
        .minusDays(1)
    return this.withDayOfMonth(lastDayOfMonth.dayOfMonth)
        .format(DateTimeFormatter.ofPattern(FORMAT_MONTH, Locale.US))
}

object DateFormats {
    const val FORMAT_MONTH = "MMMM"
    const val FORMAT_DAYS_MONTH = "d MMMM"
    const val FORMAT_DAYS_MONTH_TIME = "d MMMM в HH:mm"
    const val FORMAT_DAYS_MONTH_YEAR = "d MMMM yyyy"
    const val FORMAT_DAYS_MONTH_YEAR_TIME = "d MMMM yyyy в HH:mm"
    const val FORMAT_YEAR_MONTH_DAY_TIME = "yyyy-MM-ddTHH:mm:ss"
    const val FORMAT_MONTH_YEAR = "MMMM yyyy"
    const val FORMAT_TIME = "HH:mm"
    const val DOT_DAY_FORMAT = "dd.MM.yyyy"
    const val TRANSACTION_ITEM_FORMAT = "dd MMMM - HH:mm"
    val SERVER_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    val DOT_DAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(DOT_DAY_FORMAT)
}