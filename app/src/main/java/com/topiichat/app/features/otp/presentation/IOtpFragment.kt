package com.topiichat.app.features.otp.presentation

import com.topiichat.app.core.presentation.platform.IBaseFragment
import com.topiichat.app.features.otp.presentation.model.BtnSendSmsEnablingUi
import com.topiichat.app.features.otp.presentation.model.TextSendSmsTimerUi

interface IOtpFragment : IBaseFragment {
    fun onValidPinCodeRequest()
    fun onHideKeyboardEvent(nothing: Unit)
    fun onSendSms()
    fun onColorPinView(colorId: Int)
    fun onVisibilityTextError(isVisible: Boolean)
    fun onEnablingBtnSendSms(btnSendSmsEnabling: BtnSendSmsEnablingUi)
    fun onTimerTextSendSms(textSendSmsTimer: TextSendSmsTimerUi)
}