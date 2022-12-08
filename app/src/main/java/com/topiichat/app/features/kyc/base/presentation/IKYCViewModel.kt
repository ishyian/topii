package com.topiichat.app.features.kyc.base.presentation

import com.topiichat.core.presentation.platform.IBaseViewModel

interface IKYCViewModel : IBaseViewModel {
    fun onUpdateContinueBtn()
    fun onContinueClick()
}