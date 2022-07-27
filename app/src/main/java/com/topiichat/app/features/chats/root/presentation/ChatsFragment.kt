package com.topiichat.app.features.chats.root.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentChatsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatsFragment : BaseFragment<FragmentChatsBinding>(), IChatsFragment {

    @Inject
    lateinit var factory: ChatsViewModel.AssistedFactory
    private val viewModel by viewModelCreator { factory.create() }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChatsBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(imageKyc)
        initObservers()
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    private fun initObservers() = with(viewModel) {

    }
}