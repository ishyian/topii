package com.topiichat.app.features.kyc.address.presentation

import com.topiichat.app.features.kyc.base.presentation.IKYCFragment

interface IAddressFragment : IKYCFragment {
    fun onShowAddressDialog(options: List<String>)
    fun onShowCountryDialog(options: List<String>)
    fun onShowRegionDialog(options: List<String>)
}