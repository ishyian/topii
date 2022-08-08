package com.topiichat.app.features.chats.chat.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.topiichat.app.core.adapter.ListenableAsyncListDifferDelegationAdapter
import com.topiichat.app.features.chats.chat.presentation.adapter.delegates.chatIncomingTextMessageAD
import com.topiichat.app.features.chats.chat.presentation.adapter.delegates.chatOutgoingTextMessageAD
import com.topiichat.app.features.chats.chat.presentation.model.ChatMessageUiModel
import kotlin.jvm.internal.Intrinsics

class ChatMessagesAdapter : ListenableAsyncListDifferDelegationAdapter<ChatMessageUiModel>(DiffCallback) {

    init {
        delegatesManager
            .addDelegate(chatOutgoingTextMessageAD())
            .addDelegate(chatIncomingTextMessageAD())
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<ChatMessageUiModel>() {
        override fun areContentsTheSame(oldItem: ChatMessageUiModel, newItem: ChatMessageUiModel): Boolean {
            return Intrinsics.areEqual(oldItem, newItem)
        }

        override fun areItemsTheSame(oldItem: ChatMessageUiModel, newItem: ChatMessageUiModel) =
            oldItem.id == newItem.id
    }
}