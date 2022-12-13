package com.topiichat.app.features.home.presentation.adapter.transactions

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.databinding.HomeTransactionItemBinding
import com.topiichat.app.features.home.domain.model.RemittanceDomain
import com.topiichat.app.features.home.domain.model.RemittanceType
import com.topiichat.app.features.home.presentation.model.HomeTransactionUiModel
import com.topiichat.core.extension.date.DateFormats
import com.topiichat.core.extension.date.toString

@SuppressLint("SetTextI18n")
fun homeTransactionAD(
    onTransactionClick: (RemittanceDomain) -> Unit
) = adapterDelegateViewBinding<HomeTransactionUiModel, Any, HomeTransactionItemBinding>(
    { layoutInflater, parent ->
        HomeTransactionItemBinding.inflate(layoutInflater, parent, false)
    }
) {
    val radius = itemView.resources.getDimension(com.topiichat.core.R.dimen.offset15)
    itemView.setOnClickListener {
        onTransactionClick(item.transaction)
    }
    bind {
        val amountTextColorResource = when (item.transaction.action) {
            RemittanceType.REQUEST -> {
                com.topiichat.core.R.color.home_transaction_sum_profit
            }
            RemittanceType.SEND -> {
                com.topiichat.core.R.color.home_transaction_sum_expense
            }
        }
        with(binding) {
            val shapeAppearanceModel = imageAvatar.shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(radius)
                .build()
            imageAvatar.shapeAppearanceModel = shapeAppearanceModel
            Glide.with(itemView).load(item.transaction.avatar).into(imageAvatar)
            textUserName.text = item.transaction.userName
            textDate.text = item.transaction.date.toString(DateFormats.TRANSACTION_ITEM_FORMAT)
            textSum.text = item.transaction.amountText
            textSum.setTextColor(ContextCompat.getColor(itemView.context, amountTextColorResource))
        }
    }
}