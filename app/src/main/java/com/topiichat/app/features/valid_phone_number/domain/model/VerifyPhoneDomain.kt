package com.topiichat.app.features.valid_phone_number.domain.model

import com.topiichat.core.domain.Domain

data class VerifyPhoneDomain(
    val authyId: String,
    val phoneNumber: String,
    val code: String
) : Domain