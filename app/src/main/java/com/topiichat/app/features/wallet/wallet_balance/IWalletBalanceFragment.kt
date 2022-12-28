package com.topiichat.app.features.wallet.wallet_balance

import com.topiichat.app.features.home.domain.model.CurrentCountryDomain
import com.topiichat.core.presentation.platform.IBaseFragment

interface IWalletBalanceFragment : IBaseFragment {
    fun onShowBalanceImage(imageId: Int)
    fun onShowBalance(balance: String)
    fun showAddCardDialog()
    fun onAvailableFeaturesLoaded(featuresDomain: CurrentCountryDomain)
}