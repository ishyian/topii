package com.topiichat.app.features.send_remittance.presentation.adapter.delegates

import android.annotation.SuppressLint
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.R
import com.topiichat.app.databinding.RecentUserItemBinding
import com.topiichat.app.features.send_remittance.presentation.model.RecentUserUiModel

@SuppressLint("SetTextI18n")
fun recentUserAD(
    onRecentUserClick: (RecentUserUiModel) -> Unit
) = adapterDelegateViewBinding<RecentUserUiModel, Any, RecentUserItemBinding>(
    { layoutInflater, parent ->
        RecentUserItemBinding.inflate(layoutInflater, parent, false)
    }
) {
    val radius = itemView.resources.getDimension(R.dimen.offset15)
    itemView.setOnClickListener {
        onRecentUserClick(item)
    }
    bind {
        with(binding) {
            if (item.isSelected) {
                imageAvatar.setStrokeColorResource(R.color.send_payment_currency_divider)
            } else {
                imageAvatar.setStrokeColorResource(android.R.color.transparent)
            }
            val shapeAppearanceModel = imageAvatar.shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(radius)
                .build()
            imageAvatar.shapeAppearanceModel = shapeAppearanceModel
            Glide.with(itemView).load(item.data.avatar).into(imageAvatar)
        }
    }
}