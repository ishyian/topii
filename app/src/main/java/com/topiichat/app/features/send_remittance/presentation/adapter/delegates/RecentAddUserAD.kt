package com.topiichat.app.features.send_remittance.presentation.adapter.delegates

import android.annotation.SuppressLint
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.databinding.RecentAddUserItemBinding
import com.topiichat.app.features.send_remittance.presentation.model.RecentAddUserUiModel

@SuppressLint("SetTextI18n")
fun recentAddUserAD(
    onRecentAddUserClick: () -> Unit
) = adapterDelegateViewBinding<RecentAddUserUiModel, Any, RecentAddUserItemBinding>(
    { layoutInflater, parent ->
        RecentAddUserItemBinding.inflate(layoutInflater, parent, false)
    }
) {
    itemView.setOnClickListener {
        onRecentAddUserClick()
    }
}