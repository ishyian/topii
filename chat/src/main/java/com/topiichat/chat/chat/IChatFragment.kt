package com.topiichat.chat.chat

import com.topiichat.core.presentation.platform.IBaseFragment

interface IChatFragment : IBaseFragment {
    fun onMoreDialogShow(ignore: Unit)
}