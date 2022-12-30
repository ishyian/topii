package com.topiichat.app.features.personal_information.presentation

import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.core.presentation.platform.IBaseFragment

interface IPersonalInfoFragment : IBaseFragment {
    fun onBtnContinueUiStateChanged(uiState: BtnContinueUiState)
}