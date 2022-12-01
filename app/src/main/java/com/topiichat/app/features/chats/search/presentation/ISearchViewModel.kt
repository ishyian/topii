package com.topiichat.app.features.chats.search.presentation

import com.topiichat.app.core.presentation.platform.IBaseViewModel
import eu.siacs.conversations.services.XmppConnectionService

interface ISearchViewModel : IBaseViewModel {
    fun search(query: List<String>, xmppConnectionService: XmppConnectionService)
}