package com.topiichat.app.core.extension

import java.util.Calendar
import java.util.Date

fun Date?.isValidBirthday() = this?.let { it.before(minValidBirthday) && it.after(maxValidBirthday) } ?: false

const val MIN_USER_AGE = 5
const val MAX_USER_AGE = 150

val minValidBirthday: Date
    get() = Calendar.getInstance().apply { add(Calendar.YEAR, -MIN_USER_AGE) }.time

val maxValidBirthday: Date
    get() = Calendar.getInstance().apply { add(Calendar.YEAR, -MAX_USER_AGE) }.time
