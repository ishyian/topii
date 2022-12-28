package com.topiichat.app.features.pin_code.domain.model

import com.topiichat.core.domain.Domain

data class ValidPinCodeDomain(
    val status: String,
    val pin: String
) : Domain
