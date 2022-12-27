package com.topiichat.app.features.otp.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class ValidOtpCodeRequestDto(
    @Json(name = "authyId")
    val authyId: String?,
    @Json(name = "otpCode")
    val otpCode: String?
) : Dto