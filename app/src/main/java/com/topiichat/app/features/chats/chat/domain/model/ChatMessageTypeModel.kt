package com.topiichat.app.features.chats.chat.domain.model

sealed class ChatMessageTypeModel {
    data class Incoming(
        val name: String?,
        val avatar: String?
    ) : ChatMessageTypeModel()

    object Outgoing : ChatMessageTypeModel()

    object Status : ChatMessageTypeModel()
}