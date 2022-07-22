package com.topiichat.app.features.kyc.email.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentEnterEmailBinding
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterEmailFragment : BaseFragment<FragmentEnterEmailBinding>(), IEnterEmailFragment {

    private val viewModel: EnterEmailViewModel by viewModels()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentEnterEmailBinding {
        return FragmentEnterEmailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        editTextEmail.doAfterTextChanged { text ->
            viewModel.onEmailChanged(text.toString())
        }
        setupClickListener(btnContinue, toolbar.btnClose, toolbar.btnBack)
        initObservers()
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onBtnContinueUiStateChanged(uiState: BtnContinueUiState) = with(binding.btnContinue) {
        isEnabled = uiState.isEnabled
        setBackgroundResource(uiState.backgroundId)
    }

    private fun initObservers() = with(viewModel) {
        observe(viewModel.btnContinueUiState, ::onBtnContinueUiStateChanged)
    }
}