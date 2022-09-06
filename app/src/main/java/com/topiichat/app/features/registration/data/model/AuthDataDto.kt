package com.topiichat.app.features.registration.data.model

import com.topiichat.app.core.data.Dto

data class AuthDataDto(
    val token: String?,
    val senderId: String?,
    val isoCode: String?
) : Dto