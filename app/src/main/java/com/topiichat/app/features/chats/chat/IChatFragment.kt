package com.topiichat.app.features.chats.chat

import com.topiichat.app.core.presentation.platform.IBaseFragment

interface IChatFragment : IBaseFragment {
    fun onMoreDialogShow(ignore: Unit)
}