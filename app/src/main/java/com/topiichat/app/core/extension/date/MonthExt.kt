package com.topiichat.app.core.extension.date

import org.threeten.bp.Month
import org.threeten.bp.format.TextStyle
import java.util.Locale

fun Month.getMonthName(): String {
    return getDisplayName(TextStyle.FULL_STANDALONE, Locale.US).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.ROOT
        ) else it.toString()
    }
}