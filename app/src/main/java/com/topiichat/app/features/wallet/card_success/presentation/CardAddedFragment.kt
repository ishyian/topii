package com.topiichat.app.features.wallet.card_success.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentCardAddedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardAddedFragment : BaseFragment<FragmentCardAddedBinding>(), ICardAddedFragment {

    private val viewModel: CardAddedViewModel by viewModels()

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCardAddedBinding {
        return FragmentCardAddedBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(btnOk, imageBack)
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit
}