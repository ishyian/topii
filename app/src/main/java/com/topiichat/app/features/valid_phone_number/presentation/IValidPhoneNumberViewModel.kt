package com.topiichat.app.features.valid_phone_number.presentation

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.valid_phone_number.domain.model.ValidPhone
import com.topiichat.app.features.valid_phone_number.domain.model.VerifyPhoneDomain
import com.topiichat.app.features.valid_phone_number.presentation.model.PhoneNumber

interface IValidPhoneNumberViewModel {
    fun onVerifyPhoneNumberRequest(phoneNumber: PhoneNumber)
    fun onRenderVerifyPhoneNumber(result: ResultData<VerifyPhoneDomain>)
    fun onEmptyPhoneNumber()
    fun onNotMatchPinCode()
    fun onFailValidPhoneNumber(failValidPhone: ValidPhone.Fail)
    fun onNextAfterValidate(phoneNumber: String, authyId: String, code: String)
}