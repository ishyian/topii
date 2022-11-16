package com.topiichat.app.features.chats.root.presentation

import com.topiichat.app.core.presentation.platform.IBaseFragment

interface IChatsFragment : IBaseFragment {
    fun onContentLoaded(content: List<Any>)
    fun refresh()
    fun onStartChat(ignore: Boolean)
}