package com.topiichat.app.features.home.presentation.model

import com.topiichat.app.features.home.domain.model.RemittanceDomain

data class HomeTransactionUiModel(
    val transaction: RemittanceDomain
)