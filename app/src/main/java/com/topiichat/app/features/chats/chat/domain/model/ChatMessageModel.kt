package com.topiichat.app.features.chats.chat.domain.model

import java.util.Date

sealed class ChatMessageModel(
    open val id: String?,
    open val type: ChatMessageTypeModel,
    open val dateTime: Date
) {
    data class Text(
        override val id: String,
        override val type: ChatMessageTypeModel,
        override val dateTime: Date,
        val text: String
    ) : ChatMessageModel(id, type, dateTime)

    data class Status(
        override val dateTime: Date,
        val status: String
    ) : ChatMessageModel(null, ChatMessageTypeModel.Status, dateTime)
}