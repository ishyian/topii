package com.topiichat.app.features.chats.chat.presentation.model

import com.topiichat.app.features.chats.chat.domain.model.MessageType

sealed class ChatMessageUiModel(
    open val id: String?,
    open val type: MessageType,

    ) {
    data class Text(
        override val id: String,
        override val type: MessageType,
        override val date: String,
        override val text: String
    ) : ChatMessageUiModel(id, type), ChatMessageViewModel

    data class Status(
        val text: String
    ) : ChatMessageUiModel(null, MessageType.Status)

    object NextPageLoading : ChatMessageUiModel("", MessageType.Status)
    object NextPageLoadingError : ChatMessageUiModel("", MessageType.Status)
}