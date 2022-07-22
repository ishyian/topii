package com.topiichat.app.features.kyc.birthdate.presentation

import com.topiichat.app.features.kyc.base.presentation.IKYCViewModel

interface IBirthDateViewModel : IKYCViewModel {
    fun onBirthPlaceClick()
    fun onBirthPlaceChanged(birthPlace: String)
    fun onBirthDateChanged(value: String)
}