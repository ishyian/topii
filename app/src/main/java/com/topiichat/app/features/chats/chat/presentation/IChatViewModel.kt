package com.topiichat.app.features.chats.chat.presentation

import com.topiichat.app.core.presentation.platform.IBaseViewModel

interface IChatViewModel : IBaseViewModel {
    fun onSendClick()
    fun onInputChanged(input: String)
    fun onScrolled()
}