package com.topiichat.app.features.valid_phone_number.presentation

import com.topiichat.app.features.valid_phone_number.domain.model.ValidPhone

interface IValidPhoneNumberViewModel {
    fun onValidPhoneNumberRequest(phoneNumber: String)
    fun onRenderValidPhoneNumber(validPhone: ValidPhone)
    fun onSuccessValidPhoneNumber(successValidPhone: ValidPhone.Success)
    fun onEmptyPhoneNumber()
    fun onNotMatchPinCode()
    fun onFailValidPhoneNumber(failValidPhone: ValidPhone.Fail)
    fun onNextAfterValidate(phoneNumber: String)
}