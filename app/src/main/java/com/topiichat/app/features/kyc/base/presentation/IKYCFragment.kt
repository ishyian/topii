package com.topiichat.app.features.kyc.base.presentation

import com.topiichat.app.core.presentation.platform.IBaseFragment
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState

interface IKYCFragment : IBaseFragment {
    fun onBtnContinueUiStateChanged(uiState: BtnContinueUiState)
}