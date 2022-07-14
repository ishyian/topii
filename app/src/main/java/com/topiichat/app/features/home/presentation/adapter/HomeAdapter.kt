package com.topiichat.app.features.home.presentation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.topiichat.app.core.adapter.BaseDiffCallback
import com.topiichat.app.features.home.domain.model.TransactionDomain
import com.topiichat.app.features.home.presentation.adapter.transactions.homeTransactionAD
import com.topiichat.app.features.home.presentation.adapter.transactions.homeTransactionsHeaderAD

class HomeAdapter(
    onFiltersClick: () -> Unit,
    onTransactionClick: (TransactionDomain) -> Unit
) : AsyncListDifferDelegationAdapter<Any>(DiffCallback) {

    init {
        delegatesManager
            .addDelegate(homeTransactionsHeaderAD(onFiltersClick))
            .addDelegate(homeTransactionAD(onTransactionClick))
    }

    private companion object DiffCallback : BaseDiffCallback() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = when {
            else -> oldItem.javaClass == newItem.javaClass
        }
    }
}