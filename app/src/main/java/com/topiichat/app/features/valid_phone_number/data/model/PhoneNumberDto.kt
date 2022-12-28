package com.topiichat.app.features.valid_phone_number.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class PhoneNumberDto(
    @Json(name = "dialCountryCode")
    val dialCountryCode: String,
    @Json(name = "mobileNumber")
    val mobileNumber: String
) : Dto