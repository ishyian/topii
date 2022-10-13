package com.topiichat.app.features.wallet

import com.topiichat.app.features.wallet.bank_account.presentation.AddBankAccountFragment
import com.topiichat.app.features.wallet.card.presentation.AddCardBottomSheetFragment
import com.topiichat.app.features.wallet.card_success.presentation.CardAddedFragment
import com.topiichat.app.features.wallet.wallet_balance.WalletBalanceFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object WalletScreens {
    object AddBankAccount : SupportAppScreen() {
        override fun getFragment() = AddBankAccountFragment()
    }

    object CardAdded : SupportAppScreen() {
        override fun getFragment() = CardAddedFragment()
    }

    object WalletBalance : SupportAppScreen() {
        override fun getFragment() = WalletBalanceFragment()
    }

    object AddCard : SupportAppScreen() {
        override fun getFragment() = AddCardBottomSheetFragment()
    }
}