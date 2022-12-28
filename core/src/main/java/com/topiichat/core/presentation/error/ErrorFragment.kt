package com.topiichat.core.presentation.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.topiichat.core.databinding.FragmentErrorBinding
import com.topiichat.core.delegates.parcelableParameters
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.error.model.ErrorUiModel
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ErrorFragment : BaseFragment<FragmentErrorBinding>(), IErrorFragment {

    @Inject
    lateinit var factory: ErrorViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create(parameters)
    }
    private val parameters: ErrorParameters by parcelableParameters()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentErrorBinding {
        return FragmentErrorBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(btnReturn, imageBack)
        initObservers()
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onErrorModelReceived(errorUiModel: ErrorUiModel) = with(binding) {
        imageErrorIcon.setImageResource(errorUiModel.icon)
        textErrorTitle.text = getString(errorUiModel.title)
        textErrorDescription.text = getString(errorUiModel.subtitle)
        btnReturn.text = getString(errorUiModel.buttonText)
    }

    private fun initObservers() = with(viewModel) {
        observe(errorUiModel, ::onErrorModelReceived)
    }
}