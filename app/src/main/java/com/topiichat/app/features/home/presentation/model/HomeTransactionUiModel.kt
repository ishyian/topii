package com.topiichat.app.features.home.presentation.model

import com.topiichat.app.features.home.domain.model.TransactionDomain

data class HomeTransactionUiModel(
    val transaction: TransactionDomain
)