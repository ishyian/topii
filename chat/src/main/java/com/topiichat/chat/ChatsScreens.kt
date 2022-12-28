package com.topiichat.chat

import com.topiichat.chat.chat_contacts.presentation.ChatContactsFragment
import com.topiichat.chat.chat_list.presentation.ChatsFragment
import com.topiichat.chat.search.presentation.SearchFragment
import com.topiichat.chat.search.presentation.SearchParameters
import com.topiichat.core.delegates.parcelableParametersBundleOf
import com.topiichat.core.presentation.contacts.presentation.ContactsFragment
import com.topiichat.core.presentation.contacts.presentation.ContactsParameters
import ru.terrakok.cicerone.android.support.FragmentParams
import ru.terrakok.cicerone.android.support.SupportAppScreen

object ChatsScreens {
    object ChatsList : SupportAppScreen() {
        override fun getFragment() = ChatsFragment()
    }

    class Search(
        private val parameters: SearchParameters
    ) : SupportAppScreen() {
        override fun getFragment() = SearchFragment()
        override fun getFragmentParams() = FragmentParams(
            SearchFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
    }

    object ChatContacts : SupportAppScreen() {
        override fun getFragment() = ChatContactsFragment()
    }

    class Contacts(
        private val parameters: ContactsParameters
    ) : SupportAppScreen() {
        override fun getFragment() = ContactsFragment()
        override fun getFragmentParams() = FragmentParams(
            ContactsFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
    }
}