package com.topiichat.app.features.registration.domain.model

import com.topiichat.core.domain.Domain

data class AuthDataDomain(
    val token: String,
    val senderId: String,
    val isoCode: String
) : Domain {
    val accessToken get() = "Bearer $token"
}