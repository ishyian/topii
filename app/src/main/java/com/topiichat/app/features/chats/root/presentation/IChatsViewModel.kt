package com.topiichat.app.features.chats.root.presentation

import com.topiichat.app.core.presentation.platform.IBaseViewModel
import com.topiichat.app.features.chats.root.presentation.model.ChatAction
import com.topiichat.app.features.chats.root.presentation.model.ChatItemUiModel

interface IChatsViewModel : IBaseViewModel {
    fun onKYCClick()
    fun onChatItemClick(chatItem: ChatItemUiModel)
    fun onChatActionClick(action: ChatAction)
}