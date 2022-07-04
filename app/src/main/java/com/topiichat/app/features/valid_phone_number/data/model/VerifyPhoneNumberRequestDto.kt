package com.topiichat.app.features.valid_phone_number.data.model

import com.squareup.moshi.Json
import com.topiichat.app.core.data.Dto

data class VerifyPhoneNumberRequestDto(
    @Json(name = "countryIso")
    val countryIso: String,
    @Json(name = "phoneNumber")
    val phoneNumber: PhoneNumberDto
) : Dto