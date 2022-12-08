package com.topiichat.app.features.valid_phone_number.presentation.model

import android.content.Context
import com.google.android.material.textfield.TextInputEditText
import com.topiichat.core.extension.getRegionIsoCode
import com.topiichat.core.extension.parsePhoneNumber

data class PhoneNumber(
    val number: String?,
    val code: String?,
    val isoCode: String?
)

fun TextInputEditText.getPhoneNumber(context: Context): PhoneNumber {
    val unformatted = text.toString().filter { !it.isWhitespace() && it != '-' }
    val phoneNumber = unformatted.parsePhoneNumber(context)
    val isoCode = phoneNumber.getRegionIsoCode(context)
    return PhoneNumber(
        number = phoneNumber?.nationalNumber?.toString(),
        code = "+${phoneNumber?.countryCode?.toString()}",
        isoCode = isoCode
    )
}