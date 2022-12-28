package com.topiichat.app.features.otp.presentation

import com.topiichat.app.features.otp.domain.model.ResendOtpCodeDomain
import com.topiichat.app.features.otp.domain.model.SendSms
import com.topiichat.app.features.otp.domain.model.ValidOtp
import com.topiichat.app.features.otp.domain.model.ValidOtpCodeDomain
import com.topiichat.core.domain.ResultData

interface IOtpViewModel {
    fun onValidOtpCodeRequest(otpCode: String)
    fun onRenderValidPinCode(result: ResultData<ValidOtpCodeDomain>)
    fun onSuccessValidPinCode()
    fun onSmallLengthValidPinCode()
    fun onWrongValidPinCode()
    fun onFailValidPinCode(failValidPinCode: ValidOtp.Fail)
    fun onSendSms()
    fun onRenderSendSms(result: ResultData<ResendOtpCodeDomain>)
    fun onSuccessSendSms()
    fun onFailSendSms(failSendSms: SendSms.Fail)
    fun onCounterSendSms()
    fun onStartTimer()
}