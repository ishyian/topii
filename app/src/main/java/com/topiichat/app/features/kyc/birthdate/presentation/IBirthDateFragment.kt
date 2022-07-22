package com.topiichat.app.features.kyc.birthdate.presentation

import com.topiichat.app.features.kyc.base.presentation.IKYCFragment

interface IBirthDateFragment : IKYCFragment {
    fun onShowBirthPlaceDialog(options: List<String>)
    fun onShowMessageError(message: String)
}