package com.topiichat.chat.chat_list.presentation

import com.topiichat.chat.chat_list.presentation.model.ChatAction
import com.topiichat.core.presentation.platform.IBaseViewModel

interface IChatsViewModel : IBaseViewModel {
    fun onKYCClick()
    fun onChatActionClick(action: ChatAction)
    fun onSearchClick()
}