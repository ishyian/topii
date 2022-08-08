package com.topiichat.app.features.chats.chat.domain.model

sealed class MessageType {
    data class Incoming(
        val name: String?,
        val avatar: String?
    ) : MessageType()

    object Status : MessageType()
    object Outgoing : MessageType()
}