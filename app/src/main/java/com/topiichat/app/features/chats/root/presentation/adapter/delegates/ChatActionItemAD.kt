package com.topiichat.app.features.chats.root.presentation.adapter.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.R
import com.topiichat.app.databinding.ChatActionItemBinding
import com.topiichat.app.features.chats.root.presentation.model.ChatAction
import com.topiichat.app.features.chats.root.presentation.model.ChatActionUiModel

fun chatActionItemAD(
    onChatActionClick: (ChatAction) -> Unit
) = adapterDelegateViewBinding<ChatActionUiModel, Any, ChatActionItemBinding>(
    { layoutInflater, parent ->
        ChatActionItemBinding.inflate(layoutInflater, parent, false)
    }
) {
    itemView.setOnClickListener {
        onChatActionClick(item.action)
    }
    bind {
        binding.textActionName.text = when (item.action) {
            ChatAction.CREATE_GROUP -> {
                context.getString(R.string.chats_action_create_group)
            }
            ChatAction.SEARCH_BY_PHONE -> {
                context.getString(R.string.chats_action_search_by_phone)
            }
            ChatAction.INVITE_FRIENDS -> {
                context.getString(R.string.chats_action_invite_friends)
            }
        }
    }
}