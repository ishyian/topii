package com.topiichat.app.features.valid_phone_number.data

import com.squareup.moshi.Json
import com.topiichat.app.core.data.Dto

data class ValidPhoneNumberDto(
    @Json(name = "success")
    val success: Boolean?,
    @Json(name = "message")
    val message: String?
): Dto