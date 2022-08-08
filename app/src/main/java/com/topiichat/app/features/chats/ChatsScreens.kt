package com.topiichat.app.features.chats

import com.topiichat.app.core.delegates.parcelableParametersBundleOf
import com.topiichat.app.features.chats.chat.presentation.ChatFragment
import com.topiichat.app.features.chats.chat.presentation.ChatParameters
import com.topiichat.app.features.chats.root.presentation.ChatsFragment
import ru.terrakok.cicerone.android.support.FragmentParams
import ru.terrakok.cicerone.android.support.SupportAppScreen

object ChatsScreens {
    object ChatsList : SupportAppScreen() {
        override fun getFragment() = ChatsFragment()
    }

    class Chat(private val parameters: ChatParameters) : SupportAppScreen() {
        override fun getFragment() = ChatFragment()
        override fun getFragmentParams() = FragmentParams(
            ChatFragment::class.java,
            parcelableParametersBundleOf(parameters)
        )
    }
}