package com.topiichat.app.features.registration.domain.model

import com.topiichat.core.domain.Domain

class RegisterDomain(
    val accessToken: String,
    val senderId: String
) : Domain