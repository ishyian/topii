package com.topiichat.app.features.chats.search.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import com.topiichat.app.R
import com.topiichat.app.core.extension.getDrawableKtx
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentSearchBinding
import com.topiichat.app.features.chats.activity.ChatsActivity
import com.topiichat.app.features.chats.search.presentation.adapter.SearchMessagesAdapter
import dagger.hilt.android.AndroidEntryPoint
import eu.siacs.conversations.entities.Message
import eu.siacs.conversations.ui.interfaces.OnBackendConnected
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(),
    ISearchFragment,
    OnBackendConnected,
    SearchMessagesAdapter.OnMessageClickListener {

    @Inject
    lateinit var factory: SearchViewModel.AssistedFactory
    private val viewModel by viewModelCreator { factory.create() }

    private val chatsActivity by lazy {
        requireActivity() as ChatsActivity
    }

    private val messages = arrayListOf<Message>()

    private val searchMessagesAdapter: SearchMessagesAdapter by lazy {
        SearchMessagesAdapter(chatsActivity, messages)
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSearchBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        val dividerDrawable = requireContext().getDrawableKtx(R.drawable.chats_list_divider)
        val itemDecoration = DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )
        dividerDrawable?.let {
            itemDecoration.setDrawable(it)
        }
        searchMessagesAdapter.setConversationClickListener(this@SearchFragment)
        rvMessages.run {
            addItemDecoration(itemDecoration)
            adapter = searchMessagesAdapter
        }
        initObservers()
        viewModel.search("ih", chatsActivity.xmppConnectionService)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onBackendConnected() {

    }

    override fun onMessageClick(view: View?, message: Message?) {
        val conversation =
            chatsActivity.xmppConnectionService.findConversationByUuid(message?.conversationUuid)
        chatsActivity.openConversation(conversation, null)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSearchResultsLoaded(messages: List<Message>) {
        this.messages.clear()
        this.messages.addAll(messages)
        searchMessagesAdapter.notifyDataSetChanged()
    }

    private fun initObservers() = with(viewModel) {
        observe(searchResults, ::onSearchResultsLoaded)
    }
}