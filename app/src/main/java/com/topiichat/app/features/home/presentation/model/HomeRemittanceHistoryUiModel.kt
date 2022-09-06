package com.topiichat.app.features.home.presentation.model

data class HomeRemittanceHistoryUiModel(
    val totalSum: Long,
    val remittances: List<HomeTransactionUiModel>,
    val totalReceived: Long,
    val totalSent: Long,
    val currency: String
)