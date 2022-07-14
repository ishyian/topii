package com.topiichat.app.features.home.domain.model

import com.topiichat.app.core.domain.Domain

data class TransactionDomain(
    val userName: String,
    val date: String,
    val amount: Double
) : Domain