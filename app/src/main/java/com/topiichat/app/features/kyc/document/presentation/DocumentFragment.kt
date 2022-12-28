package com.topiichat.app.features.kyc.document.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.topiichat.app.databinding.FragmentDocumentBinding
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocumentFragment : BaseFragment<FragmentDocumentBinding>(), IDocumentFragment {

    private val viewModel: DocumentViewModel by viewModels()

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDocumentBinding {
        return FragmentDocumentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        editTextDocument.doAfterTextChanged { text ->
            viewModel.onDocumentChanged(text.toString())
        }
        setupClickListener(btnContinue, toolbar.btnBack, toolbar.btnClose)
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