package com.topiichat.chat.chat_contacts.presentation.adapter

import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.topiichat.chat.chat_contacts.presentation.adapter.delegates.chatContactItemAD
import com.topiichat.chat.chat_contacts.presentation.model.ChatContactUiModel
import com.topiichat.core.adapter.BaseDiffCallback

class ChatsContactsAdapter(
    onChatContactClick: (ChatContactUiModel) -> Unit,
) : AsyncListDifferDelegationAdapter<Any>(DiffCallback) {

    init {
        delegatesManager
            .addDelegate(chatContactItemAD(onChatContactClick))
    }

    private companion object DiffCallback : BaseDiffCallback() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any) = when {
            else -> oldItem.javaClass == newItem.javaClass
        }
    }
}