package com.topiichat.chat

import com.topiichat.chat.chat_list.presentation.ChatsFragment
import com.topiichat.chat.search.presentation.SearchFragment
import com.topiichat.chat.search.presentation.SearchParameters
import com.topiichat.core.delegates.parcelableParametersBundleOf
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
}