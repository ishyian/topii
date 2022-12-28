package com.topiichat.app.features.send_remittance.presentation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.topiichat.app.features.send_remittance.presentation.adapter.delegates.recentAddUserAD
import com.topiichat.app.features.send_remittance.presentation.adapter.delegates.recentUserAD
import com.topiichat.app.features.send_remittance.presentation.model.RecentUserUiModel
import com.topiichat.core.adapter.BaseDiffCallback

class RecentUsersAdapter(
    onUserClick: (RecentUserUiModel) -> Unit,
    onAddUserClick: () -> Unit
) : AsyncListDifferDelegationAdapter<Any>(DiffCallback) {

    init {
        delegatesManager
            .addDelegate(recentAddUserAD(onAddUserClick))
            .addDelegate(recentUserAD(onUserClick))
    }

    private companion object DiffCallback : BaseDiffCallback() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = when {
            else -> oldItem.javaClass == newItem.javaClass
        }

        override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
            return when {
                oldItem is RecentUserUiModel && newItem is RecentUserUiModel -> {
                    return listOf(
                        newItem.isSelected
                    )
                }
                else -> super.getChangePayload(oldItem, newItem)
            }
        }
    }
}