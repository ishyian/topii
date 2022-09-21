package com.topiichat.app.features.kyc.email.presentation

import com.topiichat.app.features.kyc.base.presentation.IKYCViewModel

interface IEnterEmailViewModel : IKYCViewModel {
    fun onEmailChanged(value: String)
    fun onSuccessVerification(userId: String)
}