package com.topiichat.app.features.splash.data.model

import com.topiichat.app.core.data.Dto

data class TokenCacheDto(
    val token: String,
    val expireOfSeconds: Int
) : Dto