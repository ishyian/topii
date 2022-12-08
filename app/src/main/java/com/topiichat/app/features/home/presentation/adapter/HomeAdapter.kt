package com.topiichat.app.features.home.presentation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.topiichat.app.features.home.domain.model.RemittanceDomain
import com.topiichat.app.features.home.presentation.adapter.transactions.homeTransactionAD
import com.topiichat.app.features.home.presentation.adapter.transactions.homeTransactionsHeaderAD
import com.topiichat.core.adapter.BaseDiffCallback

class HomeAdapter(
    onFiltersClick: () -> Unit,
    onTransactionClick: (RemittanceDomain) -> Unit
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