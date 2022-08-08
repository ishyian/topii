package com.topiichat.app.features.chats.chat.presentation.model

import com.topiichat.app.features.chats.chat.domain.model.MessageType

internal interface ChatMessageViewModel {
    val date: String
    val type: MessageType
    val text: String
}