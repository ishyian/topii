package com.topiichat.app.features.otp.presentation

import com.topiichat.app.features.otp.domain.model.SendSms
import com.topiichat.app.features.otp.domain.model.ValidOtp

interface IOtpViewModel {
    fun onValidPinCodeRequest(pinCode: String)
    fun onRenderValidPinCode(validPinCode: ValidOtp)
    fun onSuccessValidPinCode()
    fun onSmallLengthValidPinCode()
    fun onWrongValidPinCode()
    fun onFailValidPinCode(failValidPinCode: ValidOtp.Fail)
    fun onSendSms(phoneNumber: String)
    fun onRenderSendSms(sendSms: SendSms)
    fun onSuccessSendSms()
    fun onFailSendSms(failSendSms: SendSms.Fail)
    fun onCounterSendSms()
    fun onStartTimer()
}