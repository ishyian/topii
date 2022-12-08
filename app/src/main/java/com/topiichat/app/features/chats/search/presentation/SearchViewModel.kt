package com.topiichat.app.features.chats.search.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.core.annotations.ChatRouterQualifier
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import eu.siacs.conversations.entities.Message
import eu.siacs.conversations.services.XmppConnectionService
import eu.siacs.conversations.ui.interfaces.OnSearchResultsAvailable
import ru.terrakok.cicerone.Router
import timber.log.Timber

class SearchViewModel @AssistedInject constructor(
    @Assisted private val parameters: SearchParameters,
    @ChatRouterQualifier appRouter: Router
) : BaseViewModel(appRouter),
    ISearchViewModel,
    OnSearchResultsAvailable {

    private val _searchResults: MutableLiveData<List<Message>> = MutableLiveData()
    val searchResults: LiveData<List<Message>> = _searchResults

    override fun search(query: List<String>, uuid: String?, xmppConnectionService: XmppConnectionService) {
        xmppConnectionService.search(query, uuid, this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.text_cancel -> onClickBack()
        }
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
        fun create(
            @Assisted parameters: SearchParameters
        ): SearchViewModel
    }
}