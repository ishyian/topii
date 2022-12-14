package com.topiichat.chat.chat_contacts.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topiichat.chat.activity.ChatsActivity
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.platform.BaseFragment
import com.yourbestigor.chat.databinding.FragmentChatContactsBinding
import dagger.hilt.android.AndroidEntryPoint
import eu.siacs.conversations.entities.Account
import eu.siacs.conversations.entities.ListItem
import eu.siacs.conversations.ui.interfaces.OnBackendConnected
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ChatContactsFragment : BaseFragment<FragmentChatContactsBinding>(), OnBackendConnected {

    private val contacts = arrayListOf<ListItem>()

    @Inject
    lateinit var factory: ChatContactsViewModel.AssistedFactory
    private val viewModel by viewModelCreator { factory.create() }

    private val chatsActivity by lazy {
        requireActivity() as ChatsActivity
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChatContactsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (chatsActivity.xmppConnectionServiceBound) {
            contacts.clear()
            val accounts: List<Account> = chatsActivity.xmppConnectionService.accounts
            for (account in accounts) {
                if (account.status != Account.State.DISABLED) {
                    for (contact in account.roster.contacts) {
                        contacts.add(contact)
                    }
                }
            }
            Timber.d(contacts.toString())
        }
    }

    override fun onBackendConnected() {
        //Ignore
    }
}