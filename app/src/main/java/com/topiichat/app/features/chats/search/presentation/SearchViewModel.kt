package com.topiichat.app.features.chats.search.presentation

import android.view.View
import com.topiichat.app.core.presentation.platform.BaseViewModel
import dagger.assisted.AssistedInject
import eu.siacs.conversations.entities.Message
import eu.siacs.conversations.services.XmppConnectionService
import eu.siacs.conversations.ui.interfaces.OnSearchResultsAvailable
import eu.siacs.conversations.utils.FtsUtils
import ru.terrakok.cicerone.Router

class SearchViewModel @AssistedInject constructor(
    appRouter: Router
) : BaseViewModel(appRouter),
    ISearchViewModel,
    OnSearchResultsAvailable {

    override fun search(query: String, xmppConnectionService: XmppConnectionService) {
        xmppConnectionService.search(FtsUtils.parse(query), null, this)
    }

    override fun onClick(view: View?) {

    }

    override fun onSearchResultsAvailable(
        term: MutableList<String>?,
        messages: MutableList<Message>?
    ) {

    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): SearchViewModel
    }
}