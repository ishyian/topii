package com.topiichat.app.core.extension.date

import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetTime

fun OffsetTime.toLocalTimeWithOffset(): LocalTime {
    return toLocalTime()
        .minusSeconds(offset.totalSeconds.toLong())
        .plusSeconds(OffsetTime.now().offset.totalSeconds.toLong())
}