package com.topiichat.app.features.pin_code.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class ValidPinCodeRequestDto(
    @Json(name = "pin")
    val pin: String
) : Dto