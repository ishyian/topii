package com.topiichat.app.features.chats.root.presentation

import com.topiichat.app.core.presentation.platform.IBaseViewModel
import com.topiichat.app.features.chats.root.presentation.model.ChatAction

interface IChatsViewModel : IBaseViewModel {
    fun onKYCClick()
    fun onChatActionClick(action: ChatAction)
}