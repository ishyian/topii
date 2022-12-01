package com.topiichat.app.features.chats.search.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.core.presentation.platform.BaseViewModel
import dagger.assisted.AssistedInject
import eu.siacs.conversations.entities.Message
import eu.siacs.conversations.services.XmppConnectionService
import eu.siacs.conversations.ui.interfaces.OnSearchResultsAvailable
import ru.terrakok.cicerone.Router
import timber.log.Timber

class SearchViewModel @AssistedInject constructor(
    appRouter: Router
) : BaseViewModel(appRouter),
    ISearchViewModel,
    OnSearchResultsAvailable {

    private val _searchResults: MutableLiveData<List<Message>> = MutableLiveData()
    val searchResults: LiveData<List<Message>> = _searchResults

    override fun search(query: List<String>, xmppConnectionService: XmppConnectionService) {
        xmppConnectionService.search(query, null, this)
    }

    override fun onClick(view: View?) {

    }

    override fun onSearchResultsAvailable(
        term: MutableList<String>?,
        messages: MutableList<Message>?
    ) {
        Timber.d(messages?.size.toString())
        messages?.let {
            _searchResults.postValue(messages)
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): SearchViewModel
    }
}