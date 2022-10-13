package com.topiichat.app.data.features.registration.utils

import com.topiichat.app.features.registration.data.model.RegisterDto
import com.topiichat.app.features.registration.domain.model.RegisterDomain

object RegisterTestUtils {
    val dto = RegisterDto(
        accessToken = "token",
        badgeCount = null,
        dialCountryCode = null,
        firebaseUID = null,
        id = null,
        isActive = true,
        nickName = null,
        phoneNumber = null,
        profile = null,
        pushKitToken = null,
        pushToken = null,
        streamToken = null
    )
    val domain = RegisterDomain(
        accessToken = "token",
        senderId = "senderId"
    )
}