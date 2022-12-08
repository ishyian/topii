package com.topiichat.app.features.kyc.base.presentation

import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.core.presentation.platform.IBaseFragment

interface IKYCFragment : IBaseFragment {
    fun onBtnContinueUiStateChanged(uiState: BtnContinueUiState)
}