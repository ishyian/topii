package com.topiichat.app.features.wallet.bank_account.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.topiichat.app.R
import com.topiichat.app.core.extension.setupInputMask
import com.topiichat.app.core.extension.showSelectorDialog
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentAddBankAccountBinding
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddBankAccountFragment : BaseFragment<FragmentAddBankAccountBinding>(), IAddBankAccountFragment {
    @Inject
    lateinit var factory: AddBankAccountViewModel.AssistedFactory
    private val viewModel by viewModelCreator { factory.create() }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAddBankAccountBinding {
        return FragmentAddBankAccountBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(toolbar.btnBack, layoutBank, btnContinue)
        textIban.editText.doAfterTextChanged { text ->
            text?.let { viewModel.onIbanChanged(it.toString()) }
        }
        textBic.editText.doAfterTextChanged { text ->
            text?.let { viewModel.onBicChanged(it.toString()) }
        }
        initObservers()
    }

    override fun onShowBankDialog(options: List<String>) {
        showSelectorDialog(getString(R.string.wallet_add_account_bank_name_title), options) { _, position ->
            binding.textBankName.text = options[position]
            viewModel.onBankChanged(options[position])
        }
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onBtnContinueUiStateChanged(uiState: BtnContinueUiState) = with(binding.btnContinue) {
        isEnabled = uiState.isEnabled
        setBackgroundResource(uiState.backgroundId)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    private fun initObservers() = with(viewModel) {
        observe(btnContinueUiState, ::onBtnContinueUiStateChanged)
        observe(showBankDialog, ::onShowBankDialog)
        observe(setupIbanInputMask, ::onSetupIbanInputMask)
    }

    private fun onSetupIbanInputMask(inputMask: String) = with(binding.textIban) {
        editText.setupInputMask(inputMask, true)
    }
}