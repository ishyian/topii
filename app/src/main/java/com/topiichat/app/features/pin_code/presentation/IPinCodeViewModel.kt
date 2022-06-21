package com.topiichat.app.features.pin_code.presentation

import com.topiichat.app.features.pin_code.domain.ValidPinCode

interface IPinCodeViewModel {
    fun onClickShowPass()
    fun onCheckPinCode(pinCode: String)
    fun onCheckValidPinCode(pinCode: String): ValidPinCode
    fun onConfirmPinCode(pinCode: String): ValidPinCode
    fun onRenderValidPinCode(validPinCode: ValidPinCode)
    fun onSuccessValidPinCode(pinCode: String)
    fun onEmptyValidPinCode()
    fun onErrorCountSymbolsValidPinCode()
    fun onErrorConsecutiveNumbersValidPinCode()
    fun onErrorSameNumbersValidPinCode()
    fun onErrorMatches()
}