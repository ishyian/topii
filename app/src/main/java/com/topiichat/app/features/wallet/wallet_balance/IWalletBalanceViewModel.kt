package com.topiichat.app.features.wallet.wallet_balance

import com.topiichat.core.presentation.platform.IBaseViewModel

interface IWalletBalanceViewModel : IBaseViewModel {
    fun onShowBalanceClick()
    fun onAddCreditCardClick()
    fun checkAvailableCountryFeatures()
    fun navigateToAddBankAccount()
}