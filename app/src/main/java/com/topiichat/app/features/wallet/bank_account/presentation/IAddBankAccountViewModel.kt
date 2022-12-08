package com.topiichat.app.features.wallet.bank_account.presentation

import com.topiichat.core.presentation.platform.IBaseViewModel

interface IAddBankAccountViewModel : IBaseViewModel {

    fun onIbanChanged(iban: String)
    fun onBicChanged(bicCode: String)
    fun onBankClick()
    fun onBankChanged(bankName: String)
    fun onUpdateContinueBtn()
    fun onBtnOkClick()
}