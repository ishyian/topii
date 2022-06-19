package com.topiichat.app.features.splash.domain.model

import com.topiichat.app.core.domain.Domain

data class TokenDomain(
    val token: String,
    val expireOfSeconds: Int
) : Domain