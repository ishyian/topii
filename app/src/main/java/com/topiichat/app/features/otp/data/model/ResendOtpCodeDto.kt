package com.topiichat.app.features.otp.data.model

import com.squareup.moshi.Json
import com.topiichat.app.core.data.Dto

data class ResendOtpCodeDto(
    @Json(name = "success")
    val success: Boolean?,
    @Json(name = "message")
    val message: String?
) : Dto