package com.topiichat.chat.chat_contacts.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.platform.BaseFragment
import com.yourbestigor.chat.databinding.FragmentChatContactsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatContactsFragment : BaseFragment<FragmentChatContactsBinding>() {

    @Inject
    lateinit var factory: ChatContactsViewModel.AssistedFactory
    private val viewModel by viewModelCreator { factory.create() }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChatContactsBinding.inflate(inflater, container, false)

}