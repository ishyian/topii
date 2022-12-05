package com.topiichat.app.features.chats.chat

import com.topiichat.app.core.presentation.platform.IBaseViewModel

interface IChatViewModel : IBaseViewModel {
    fun onMoreDialogShow()
    fun onSearchClick(uuid: String?)
}