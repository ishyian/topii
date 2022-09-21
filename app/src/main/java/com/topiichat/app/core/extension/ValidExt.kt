package com.topiichat.app.core.extension

import android.util.Patterns
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date?.isValidBirthday() = this?.let { it.before(minValidBirthday) && it.after(maxValidBirthday) } ?: false

const val MIN_USER_AGE = 5
const val MAX_USER_AGE = 150

val minValidBirthday: Date
    get() = Calendar.getInstance().apply { add(Calendar.YEAR, -MIN_USER_AGE) }.time

val maxValidBirthday: Date
    get() = Calendar.getInstance().apply { add(Calendar.YEAR, -MAX_USER_AGE) }.time

fun String.isValidEmail() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.toIso3CountryCode(): String? {
    val locale = Locale("", this)
    return locale.isO3Country
}