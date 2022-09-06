package com.topiichat.app.features.home.domain.model

import com.topiichat.app.core.domain.Domain

data class RecentUserDomain(
    val avatar: String,
    val recipientId: String
) : Domain
