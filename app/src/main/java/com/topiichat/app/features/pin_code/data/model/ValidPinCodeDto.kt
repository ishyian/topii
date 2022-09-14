package com.topiichat.app.features.pin_code.data.model

import com.squareup.moshi.Json
import com.topiichat.app.core.data.Dto

data class ValidPinCodeDto(
    @Json(name = "pin")
    val pin: String?,
    @Json(name = "validation")
    val validation: String?
) : Dto