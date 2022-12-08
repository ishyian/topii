package com.topiichat.app.features.otp.domain.model

import com.topiichat.core.domain.Domain

data class ValidOtpCodeDomain(
    val isSuccessful: Boolean,
    val message: String
) : Domain
