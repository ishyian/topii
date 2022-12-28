package com.topiichat.app.features.home.domain.model

import com.topiichat.core.domain.Domain

data class RecentUserDomain(
    val avatar: String,
    val recipientId: String,
    val dialCode: String,
    val fullName: String
) : Domain
