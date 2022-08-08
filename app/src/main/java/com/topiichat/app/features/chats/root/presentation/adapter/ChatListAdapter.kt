package com.topiichat.app.features.chats.root.presentation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.topiichat.app.core.adapter.BaseDiffCallback
import com.topiichat.app.features.chats.root.presentation.adapter.delegates.chatActionItemAD
import com.topiichat.app.features.chats.root.presentation.adapter.delegates.chatListItemAD
import com.topiichat.app.features.chats.root.presentation.model.ChatAction
import com.topiichat.app.features.chats.root.presentation.model.ChatItemUiModel

class ChatsListAdapter(
    onChatActionClick: (ChatAction) -> Unit,
    onChatItemClick: (ChatItemUiModel) -> Unit,
) : AsyncListDifferDelegationAdapter<Any>(DiffCallback) {

    init {
        delegatesManager
            .addDelegate(chatActionItemAD(onChatActionClick))
            .addDelegate(chatListItemAD(onChatItemClick))
    }

    private companion object DiffCallback : BaseDiffCallback() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = when {
            else -> oldItem.javaClass == newItem.javaClass
        }
    }
}