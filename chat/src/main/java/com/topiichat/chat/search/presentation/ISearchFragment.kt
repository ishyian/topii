package com.topiichat.chat.search.presentation

import com.topiichat.core.presentation.platform.IBaseFragment
import eu.siacs.conversations.entities.Message

interface ISearchFragment : IBaseFragment {
    fun onSearchResultsLoaded(messages: List<Message>)
}