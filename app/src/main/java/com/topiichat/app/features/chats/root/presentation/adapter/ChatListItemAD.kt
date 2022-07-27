package com.topiichat.app.features.chats.root.presentation.adapter

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.databinding.ChatListItemBinding
import com.topiichat.app.features.chats.root.presentation.model.ChatItemUiModel

fun chatListItemAD(
    onChatClick: () -> Unit
) = adapterDelegateViewBinding<ChatItemUiModel, Any, ChatListItemBinding>(
    { layoutInflater, parent ->
        ChatListItemBinding.inflate(layoutInflater, parent, false)
    }
) {
    itemView.setOnClickListener {
        onChatClick()
    }
    bind {

    }
}