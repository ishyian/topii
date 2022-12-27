package com.topiichat.app.features.valid_phone_number.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class VerifyPhoneNumberDto(
    @Json(name = "authyId")
    val authyId: String,
    @Json(name = "phoneNumber")
    val phoneNumber: PhoneNumberDto
) : Dto