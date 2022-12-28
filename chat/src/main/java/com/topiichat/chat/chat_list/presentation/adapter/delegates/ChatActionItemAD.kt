package com.topiichat.chat.chat_list.presentation.adapter.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.chat.chat_list.presentation.model.ChatAction
import com.topiichat.chat.chat_list.presentation.model.ChatActionUiModel
import com.yourbestigor.chat.R
import com.yourbestigor.chat.databinding.ChatActionItemBinding

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