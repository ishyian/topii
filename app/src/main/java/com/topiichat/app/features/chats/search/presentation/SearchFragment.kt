package com.topiichat.app.features.chats.search.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import com.topiichat.app.R
import com.topiichat.app.databinding.FragmentSearchBinding
import com.topiichat.app.features.chats.activity.ChatsActivity
import com.topiichat.app.features.chats.search.presentation.adapter.SearchMessagesAdapter
import com.topiichat.core.delegates.parcelableParameters
import com.topiichat.core.extension.getDrawableKtx
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import eu.siacs.conversations.entities.Message
import eu.siacs.conversations.services.MessageSearchTask
import eu.siacs.conversations.ui.interfaces.OnBackendConnected
import eu.siacs.conversations.ui.util.ChangeWatcher
import eu.siacs.conversations.utils.FtsUtils
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(),
    ISearchFragment,
    OnBackendConnected,
    TextWatcher,
    SearchMessagesAdapter.OnMessageClickListener {

    @Inject
    lateinit var factory: SearchViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create(parameters)
    }
    private val parameters: SearchParameters by parcelableParameters()

    private val chatsActivity by lazy {
        requireActivity() as ChatsActivity
    }

    private val messages = arrayListOf<Message>()
    private val currentSearch = ChangeWatcher<List<String>>()

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
        editTextSearch.addTextChangedListener(this@SearchFragment)
        setupClickListener(textCancel)
        initObservers()
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onBackendConnected() {

    }

    override fun onMessageClick(view: View?, message: Message?) {
        val conversation =
            chatsActivity.xmppConnectionService.findConversationByUuid(message?.conversationUuid)
        chatsActivity.openConversation(conversation, null)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Ignore
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Ignore
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun afterTextChanged(s: Editable?) {
        val term = FtsUtils.parse(s.toString().trim())
        if (!currentSearch.watch(term)) {
            return
        }
        if (term.size > 0) {
            binding.textMessage.isVisible = false
            viewModel.search(term, parameters.uuid, chatsActivity.xmppConnectionService)
        } else {
            binding.textMessage.isVisible = true
            MessageSearchTask.cancelRunningTasks()
            messages.clear()
            searchMessagesAdapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSearchResultsLoaded(messages: List<Message>) {
        this.messages.clear()
        this.messages.addAll(messages)
        binding.textMessage.text = getString(R.string.no_results)
        binding.textMessage.isVisible = messages.isEmpty()
        searchMessagesAdapter.notifyDataSetChanged()
    }

    private fun initObservers() = with(viewModel) {
        observe(searchResults, ::onSearchResultsLoaded)
    }
}