package com.topiichat.app.features.chats.chat.presentation

import com.topiichat.app.core.presentation.platform.IBaseFragment
import com.topiichat.app.features.chats.chat.presentation.model.ChatMessageUiModel

interface IChatFragment : IBaseFragment {
    fun onMessagesLoaded(messages: List<ChatMessageUiModel>)
    fun onClearInput(ignore: Unit)
}