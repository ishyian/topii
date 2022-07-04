package com.topiichat.app.core.extension

import android.content.Context
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber

fun String.parsePhoneNumber(context: Context): Phonenumber.PhoneNumber? {
    val phoneUtil = PhoneNumberUtil.createInstance(context)
    try {
        val phoneNumber = phoneUtil.parse(this, null)
        if (!phoneUtil.isValidNumber(phoneNumber)) {
            return null
        }
        return phoneNumber
    } catch (e: NumberParseException) {
        return null
    }
}

fun Phonenumber.PhoneNumber?.getRegionIsoCode(context: Context): String? {
    return if (this == null) null
    else {
        val phoneUtil = PhoneNumberUtil.createInstance(context)
        phoneUtil.getRegionCodeForNumber(this)
    }
}