package com.topiichat.app.features.kyc.address.presentation

import com.topiichat.app.features.kyc.base.presentation.IKYCViewModel

interface IAddressViewModel : IKYCViewModel {
    fun onCityChanged(value: String)
    fun onPostalCodeChanged(value: String)
    fun onAddressChanged(value: String)
    fun onCountryChanged(value: String)
    fun onRegionChanged(value: String)
}