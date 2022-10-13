package com.topiichat.app.features.wallet.bank_account.presentation

import com.topiichat.app.core.presentation.platform.IBaseFragment
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState

interface IAddBankAccountFragment : IBaseFragment {

    fun onShowBankDialog(options: List<String>)
    fun onBtnContinueUiStateChanged(uiState: BtnContinueUiState)
}