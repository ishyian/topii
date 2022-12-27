package com.topiichat.app.features.otp.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class ResendOtpCodeRequestDto(
    @Json(name = "authyId")
    val authyId: String?
) : Dto