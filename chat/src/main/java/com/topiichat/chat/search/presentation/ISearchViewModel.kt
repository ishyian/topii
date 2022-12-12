package com.topiichat.chat.search.presentation

import com.topiichat.core.presentation.platform.IBaseViewModel
import eu.siacs.conversations.services.XmppConnectionService

interface ISearchViewModel : IBaseViewModel {
    fun search(query: List<String>, uuid: String?, xmppConnectionService: XmppConnectionService)
}