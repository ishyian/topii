package com.topiichat.app.features.splash.domain.model

import com.topiichat.core.domain.Domain

data class ValidateAppDomain(
    val accessToken: String
) : Domain
