package com.topiichat.chat.chat

import com.topiichat.core.presentation.platform.IBaseViewModel

interface IChatViewModel : IBaseViewModel {
    fun onMoreDialogShow()
    fun onSearchClick(uuid: String?)
}