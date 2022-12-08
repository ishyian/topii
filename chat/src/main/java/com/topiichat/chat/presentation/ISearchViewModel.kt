package com.topiichat.chat.presentation

import com.topiichat.core.presentation.platform.IBaseViewModel
import eu.siacs.conversations.services.XmppConnectionService

interface ISearchViewModel : IBaseViewModel {
    fun search(query: List<String>, uuid: String?, xmppConnectionService: XmppConnectionService)
}