package com.topiichat.app.features.valid_phone_number.data

import com.squareup.moshi.Json
import com.topiichat.app.core.data.Dto

data class ValidPhoneNumberRequestDto(
    @Json(name = "dialCountryCode")
    val code: String,
    @Json(name = "mobileNumber")
    val number: String
): Dto