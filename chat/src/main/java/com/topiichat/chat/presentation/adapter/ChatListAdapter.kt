package com.topiichat.chat.presentation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.topiichat.chat.presentation.adapter.delegates.chatActionItemAD
import com.topiichat.chat.presentation.model.ChatAction
import com.topiichat.core.adapter.BaseDiffCallback

class ChatsListAdapter(
    onChatActionClick: (ChatAction) -> Unit,
) : AsyncListDifferDelegationAdapter<Any>(DiffCallback) {

    init {
        delegatesManager
            .addDelegate(chatActionItemAD(onChatActionClick))
    }

    private companion object DiffCallback : BaseDiffCallback() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = when {
            else -> oldItem.javaClass == newItem.javaClass
        }
    }
}