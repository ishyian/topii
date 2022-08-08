package com.topiichat.app.features.chats.chat.presentation.adapter.delegates

import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.topiichat.app.core.extension.copyToClipboard
import com.topiichat.app.databinding.ChatListItemOutgoingTextBinding
import com.topiichat.app.features.chats.chat.domain.model.MessageType
import com.topiichat.app.features.chats.chat.presentation.model.ChatMessageUiModel

fun chatOutgoingTextMessageAD() =
    adapterDelegateViewBinding<ChatMessageUiModel.Text, ChatMessageUiModel, ChatListItemOutgoingTextBinding>(
        viewBinding = { inflater, parent ->
            ChatListItemOutgoingTextBinding.inflate(inflater, parent, false)
        },
        on = { item, _, _ ->
            item is ChatMessageUiModel.Text && item.type is MessageType.Outgoing
        }
    ) {
        binding.viewMessage.addOnLongClickListener {
            binding.viewMessage.context.copyToClipboard(item.text)
        }

        bind {
            binding.viewMessage.message = item
        }
    }