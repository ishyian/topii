package com.topiichat.app.features.wallet.bank_account.presentation

import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.core.presentation.platform.IBaseFragment

interface IAddBankAccountFragment : IBaseFragment {

    fun onShowBankDialog(options: List<String>)
    fun onBtnContinueUiStateChanged(uiState: BtnContinueUiState)
}