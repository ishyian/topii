package com.topiichat.chat.presentation

import com.topiichat.core.presentation.platform.IBaseFragment

interface IChatsFragment : IBaseFragment {
    fun onContentLoaded(content: List<Any>)
    fun refresh()
    fun onStartChat(ignore: Boolean)
}