package com.topiichat.app.features.chats.root.presentation.adapter.delegates

import androidx.core.view.isGone
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.databinding.ChatListItemBinding
import com.topiichat.app.features.chats.root.presentation.model.ChatItemUiModel

fun chatListItemAD(
    onChatClick: (ChatItemUiModel) -> Unit
) = adapterDelegateViewBinding<ChatItemUiModel, Any, ChatListItemBinding>(
    { layoutInflater, parent ->
        ChatListItemBinding.inflate(layoutInflater, parent, false)
    }
) {
    itemView.setOnClickListener {
        onChatClick(item)
    }
    bind {
        with(item) {
            binding.textMessageTime.text = date
            binding.textMessageRecipient.text = from
            binding.textMessagePreview.text = message
            if (unreadCount > 0) {
                binding.textUnreadMessagesCount.isGone = false
                binding.textUnreadMessagesCount.text = unreadCount.toString()
            }
        }
    }
}