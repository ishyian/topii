package com.topiichat.chat.chat_contacts.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topiichat.chat.activity.ChatsActivity
import com.topiichat.chat.chat_contacts.presentation.adapter.ChatsContactsAdapter
import com.topiichat.core.extension.lazyUnsynchronized
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.platform.BaseFragment
import com.yourbestigor.chat.databinding.FragmentChatContactsBinding
import dagger.hilt.android.AndroidEntryPoint
import eu.siacs.conversations.entities.Contact
import eu.siacs.conversations.entities.Conversation
import eu.siacs.conversations.ui.interfaces.OnBackendConnected
import javax.inject.Inject

@AndroidEntryPoint
class ChatContactsFragment : BaseFragment<FragmentChatContactsBinding>(), OnBackendConnected, IChatContactsFragment,
    TextWatcher {

    @Inject
    lateinit var factory: ChatContactsViewModel.AssistedFactory
    private val viewModel by viewModelCreator { factory.create() }

    private val chatsActivity by lazy {
        requireActivity() as ChatsActivity
    }

    private val chatContactsAdapter by lazyUnsynchronized {
        ChatsContactsAdapter(onChatContactClick = viewModel::onChatContactClick)
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChatContactsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(toolbar.btnBack)
        rvContacts.adapter = chatContactsAdapter
        editTextSearch.addTextChangedListener(this@ChatContactsFragment)
        initObservers()
        if (chatsActivity.xmppConnectionServiceBound) {
            viewModel.loadContacts(chatsActivity.xmppConnectionService)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //Ignore
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //Ignore
    }

    override fun afterTextChanged(s: Editable?) {
        viewModel.loadContacts(chatsActivity.xmppConnectionService, s.toString())
    }

    override fun onBackendConnected() {
        //Ignore
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onChatContactsLoaded(items: List<Any>) {
        chatContactsAdapter.items = items
    }

    override fun openChatForContact(contact: Contact) {
        val conversation: Conversation =
            chatsActivity.xmppConnectionService.findOrCreateConversation(contact.account, contact.jid, false, true)
        chatsActivity.openConversation(conversation)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    private fun initObservers() = with(viewModel) {
        observe(content, ::onChatContactsLoaded)
        observe(openChat, ::openChatForContact)
    }
}