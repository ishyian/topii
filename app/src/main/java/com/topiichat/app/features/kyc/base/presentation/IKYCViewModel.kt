package com.topiichat.app.features.kyc.base.presentation

import com.topiichat.app.core.presentation.platform.IBaseViewModel

interface IKYCViewModel : IBaseViewModel {
    fun onUpdateContinueBtn()
    fun onContinueClick()
}