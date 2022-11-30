package com.topiichat.app.features.chats.search.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentSearchBinding
import com.topiichat.app.features.chats.activity.ChatsActivity
import dagger.hilt.android.AndroidEntryPoint
import eu.siacs.conversations.ui.interfaces.OnBackendConnected
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(),
    ISearchFragment,
    OnBackendConnected {

    @Inject
    lateinit var factory: SearchViewModel.AssistedFactory
    private val viewModel by viewModelCreator { factory.create() }

    private val chatsActivity by lazy {
        requireActivity() as ChatsActivity
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSearchBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.search("query", chatsActivity.xmppConnectionService)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onBackendConnected() {

    }
}