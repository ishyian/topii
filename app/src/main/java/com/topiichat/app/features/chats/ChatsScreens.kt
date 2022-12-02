package com.topiichat.app.features.chats

import com.topiichat.app.features.chats.root.presentation.ChatsFragment
import com.topiichat.app.features.chats.search.presentation.SearchFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object ChatsScreens {
    object ChatsList : SupportAppScreen() {
        override fun getFragment() = ChatsFragment()
    }

    object Search : SupportAppScreen() {
        override fun getFragment() = SearchFragment()
    }
}