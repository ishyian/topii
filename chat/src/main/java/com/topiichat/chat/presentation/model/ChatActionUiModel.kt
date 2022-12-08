package com.topiichat.chat.presentation.model

data class ChatActionUiModel(
    val action: ChatAction
)

enum class ChatAction {
    CREATE_GROUP,
    SEARCH_BY_PHONE,
    INVITE_FRIENDS
}