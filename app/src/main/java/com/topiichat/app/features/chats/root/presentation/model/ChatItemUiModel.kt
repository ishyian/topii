package com.topiichat.app.features.chats.root.presentation.model

data class ChatItemUiModel(
    val message: String,
    val from: String,
    val date: String,
    val unreadCount: Int = 0
)