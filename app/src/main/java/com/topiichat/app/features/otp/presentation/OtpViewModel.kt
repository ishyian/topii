package com.topiichat.app.features.otp.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.otp.domain.model.SendSms
import com.topiichat.app.features.otp.domain.usecases.ValidOtpUseCase
import com.topiichat.app.features.otp.domain.model.ValidOtp
import com.topiichat.app.features.otp.domain.usecases.SendSmsUseCase
import com.topiichat.app.features.otp.presentation.model.BtnSendSmsEnablingUi
import com.topiichat.app.features.otp.presentation.model.TextSendSmsTimerUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class OtpViewModel @Inject constructor(
    private val validPinCode: ValidOtpUseCase,
    private val sendSms: SendSmsUseCase
) : BaseViewModel(), IOtpViewModel {

    private val _colorPinView: MutableLiveData<Int> = MutableLiveData()
    val colorPinView: LiveData<Int> = _colorPinView

    private val _visibilityTextError: MutableLiveData<Boolean> = MutableLiveData()
    val visibilityTextError: LiveData<Boolean> = _visibilityTextError

    private val _btnSendSmsEnabling: MutableLiveData<BtnSendSmsEnablingUi> = MutableLiveData()
    val btnSendSmsEnabling: LiveData<BtnSendSmsEnablingUi> = _btnSendSmsEnabling

    private val _textSendSmsTimer: MutableLiveData<TextSendSmsTimerUi> = MutableLiveData()
    val textSendSmsTimer: LiveData<TextSendSmsTimerUi> = _textSendSmsTimer

    private var counterSendSms: Int = 0

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_view_back -> {
                onClickBack()
            }
            R.id.image_view_close -> {
                onClickClose()
            }
        }
    }

    override fun onValidPinCodeRequest(pinCode: String) {
        if (pinCode.length < LENGTH_PIN_CODE) {
            onRenderValidPinCode(ValidOtp.SmallLength)
            return
        }
        viewModelScope.launch {
            _visibilityTextError.value = false
            _showLoader.value = true
            delay(1000)
            val request = ValidOtpUseCase.Params(pinCode)
            val result = validPinCode(request)
            onRenderValidPinCode(result)
            _showLoader.value = false
        }
    }

    override fun onRenderValidPinCode(validPinCode: ValidOtp) {
        when (validPinCode) {
            is ValidOtp.Success -> {
                onSuccessValidPinCode()
            }
            is ValidOtp.SmallLength -> {
                onSmallLengthValidPinCode()
            }
            is ValidOtp.Wrong -> {
                onWrongValidPinCode()
            }
            is ValidOtp.Fail -> {
                onFailValidPinCode(validPinCode)
            }
        }
    }

    override fun onSuccessValidPinCode() {
        _colorPinView.value = R.color.subtitle_otp
        _visibilityTextError.value = false
        _navigate.setValue(Navigator(R.id.action_otp_to_pinCode))
    }

    override fun onSmallLengthValidPinCode() {
        _colorPinView.value = R.color.subtitle_otp
        _visibilityTextError.value = false
        _showMsgError.setValue("Length pin code is small")
    }

    override fun onWrongValidPinCode() {
        _colorPinView.value = R.color.error_color
        _visibilityTextError.value = true
    }

    override fun onFailValidPinCode(failValidPinCode: ValidOtp.Fail) {
        _colorPinView.value = R.color.subtitle_otp
        _visibilityTextError.value = false
        _showMsgError.setValue(failValidPinCode.msgError)
    }

    override fun onSendSms(phoneNumber: String) {
        _textSendSmsTimer.value = TextSendSmsTimerUi(false)
        viewModelScope.launch {
            val request = SendSmsUseCase.Params(phoneNumber)
            val result = sendSms(request)
            onRenderSendSms(result)
        }
    }

    override fun onRenderSendSms(sendSms: SendSms) {
        when (sendSms) {
            is SendSms.Success -> {
                onSuccessSendSms()
            }
            is SendSms.Fail -> {
                onFailSendSms(sendSms)
            }
        }
        onCounterSendSms()
    }

    override fun onSuccessSendSms() = Unit

    override fun onFailSendSms(failSendSms: SendSms.Fail) {
        _showMsgError.setValue(failSendSms.msgError)
    }

    override fun onCounterSendSms() {
        counterSendSms++
        if (counterSendSms == MAX_SEND_SMS) {
            _btnSendSmsEnabling.value = BtnSendSmsEnablingUi(
                isEnabled = false,
                colorId = R.color.don_t_enabled_color,
            )
            onStartTimer()
        }
    }

    override fun onStartTimer() {
        viewModelScope.launch {
            repeat(TIME_BAN_SEND_SMS_OF_SECONDS) { sec ->
                _textSendSmsTimer.value = TextSendSmsTimerUi(
                    isVisible = true,
                    colorId = R.color.error_color,
                    time = sec.toString()
                )
                delay(1000)
            }
            _btnSendSmsEnabling.value = BtnSendSmsEnablingUi(
                isEnabled = true,
                colorId = R.color.subtitle_otp,
            )
            _textSendSmsTimer.value = TextSendSmsTimerUi(
                isVisible = true,
                colorId = R.color.green,
                time = "0:00"
            )
        }
    }

    override fun onClickClose() {
        _navigate.setValue(Navigator(R.id.action_otp_to_terms))
    }

    companion object {
        private const val LENGTH_PIN_CODE = 4
        private const val MAX_SEND_SMS = 5
        private const val TIME_BAN_SEND_SMS_OF_SECONDS = 10
    }
}