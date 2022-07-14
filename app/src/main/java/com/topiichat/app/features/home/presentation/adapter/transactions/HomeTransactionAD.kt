package com.topiichat.app.features.home.presentation.adapter.transactions

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.R
import com.topiichat.app.databinding.HomeTransactionItemBinding
import com.topiichat.app.features.home.domain.model.TransactionDomain
import com.topiichat.app.features.home.presentation.model.HomeTransactionUiModel

@SuppressLint("SetTextI18n")
fun homeTransactionAD(
    onTransactionClick: (TransactionDomain) -> Unit
) = adapterDelegateViewBinding<HomeTransactionUiModel, Any, HomeTransactionItemBinding>(
    { layoutInflater, parent ->
        HomeTransactionItemBinding.inflate(layoutInflater, parent, false)
    }
) {
    itemView.setOnClickListener {
        onTransactionClick(item.transaction)
    }
    bind {
        val amountTextColorResource = if (item.transaction.amount < 0.0) {
            R.color.home_transaction_sum_expense
        } else R.color.home_transaction_sum_profit
        with(binding) {
            textUserName.text = item.transaction.userName
            textDate.text = item.transaction.date
            textSum.text = "${item.transaction.amount}$"
            textSum.setTextColor(ContextCompat.getColor(itemView.context, amountTextColorResource))
        }
    }
}