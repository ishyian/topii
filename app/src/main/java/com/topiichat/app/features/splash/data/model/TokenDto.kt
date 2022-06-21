package com.topiichat.app.features.splash.data.model

import com.squareup.moshi.Json
import com.topiichat.app.core.data.Dto

data class TokenDto(
    @Json(name = "success")
    val success: Boolean?,
    @Json(name = "auth_token")
    val token: String?,
    @Json(name = "expire")
    val expireOfSeconds: Int,
): Dto