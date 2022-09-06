package com.topiichat.app.features.registration.domain.model

import com.topiichat.app.core.domain.Domain

class RegisterDomain(
    val accessToken: String,
    val senderId: String
) : Domain