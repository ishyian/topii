package com.topiichat.app.features.home.presentation

import com.topiichat.app.features.home.domain.model.TransactionDomain

interface IHomeViewModel {
    fun onSendPaymentClick()
    fun onTransactionClick(transactionDomain: TransactionDomain)
    fun onFiltersClick()
    fun onChatsClick()
}