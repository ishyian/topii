package com.topiichat.chat.presentation

import com.topiichat.chat.presentation.model.ChatAction
import com.topiichat.core.presentation.platform.IBaseViewModel

interface IChatsViewModel : IBaseViewModel {
    fun onKYCClick()
    fun onChatActionClick(action: ChatAction)
    fun onSearchClick()
}