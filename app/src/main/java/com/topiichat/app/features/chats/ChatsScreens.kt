package com.topiichat.app.features.chats

import com.topiichat.app.features.chats.root.presentation.ChatsFragment
import com.topiichat.app.features.chats.search.presentation.SearchFragment
import com.topiichat.app.features.chats.search.presentation.SearchParameters
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