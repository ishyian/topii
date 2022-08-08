package com.topiichat.app.features.registration.domain.model

import com.topiichat.app.core.domain.Domain

data class AccessTokenDomain(
    val token: String
) : Domain