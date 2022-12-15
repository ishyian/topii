package com.topiichat.chat.chat_contacts.presentation

import com.topiichat.chat.chat_contacts.presentation.model.ChatContactUiModel
import com.topiichat.core.presentation.platform.IBaseViewModel
import eu.siacs.conversations.services.XmppConnectionService

interface IChatContactsViewModel : IBaseViewModel {
    fun loadContacts(xmppConnectionService: XmppConnectionService)
    fun onChatContactClick(item: ChatContactUiModel)
}