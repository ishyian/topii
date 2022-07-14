package com.topiichat.app.features.home.presentation.adapter.transactions

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.databinding.HomeTransactionsHeaderBinding
import com.topiichat.app.features.home.presentation.model.HomeTransactionsHeaderUiModel

fun homeTransactionsHeaderAD(
    onFiltersClick: () -> Unit
) = adapterDelegateViewBinding<HomeTransactionsHeaderUiModel, Any, HomeTransactionsHeaderBinding>(
    { layoutInflater, parent ->
        HomeTransactionsHeaderBinding.inflate(layoutInflater, parent, false)
    }
) {
    binding.imageSort.setOnClickListener { onFiltersClick() }
}