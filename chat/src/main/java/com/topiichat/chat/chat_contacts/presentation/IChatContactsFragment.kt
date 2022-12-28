package com.topiichat.chat.chat_contacts.presentation

import com.topiichat.core.presentation.platform.IBaseFragment
import eu.siacs.conversations.entities.Contact

interface IChatContactsFragment : IBaseFragment {
    fun onChatContactsLoaded(items: List<Any>)
    fun openChatForContact(contact: Contact)
}