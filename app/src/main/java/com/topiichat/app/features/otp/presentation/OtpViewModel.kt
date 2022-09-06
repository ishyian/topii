package com.topiichat.app.features.otp.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.otp.domain.model.ResendOtpCodeDomain
import com.topiichat.app.features.otp.domain.model.SendSms
import com.topiichat.app.features.otp.domain.model.ValidOtp
import com.topiichat.app.features.otp.domain.model.ValidOtpCodeDomain
import com.topiichat.app.features.otp.domain.usecases.ResendSmsUseCase
import com.topiichat.app.features.otp.domain.usecases.ValidOtpUseCase
import com.topiichat.app.features.otp.presentation.model.BtnSendSmsEnablingUi
import com.topiichat.app.features.otp.presentation.model.TextSendSmsTimerUi
import com.topiichat.app.features.pin_code.presentation.PinCodeParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

class OtpViewModel @AssistedInject constructor(
    @Assisted("otpParameters") private val parameters: OtpParameters,
    private val validPinCode: ValidOtpUseCase,
    private val sendSms: ResendSmsUseCase,
    appRouter: Router
) : BaseViewModel(appRouter), IOtpViewModel {

    private val _colorPinView: MutableLiveData<Int> = MutableLiveData()
    val colorPinView: LiveData<Int> = _colorPinView

    private val _visibilityTextError: MutableLiveData<Boolean> = MutableLiveData()
    val visibilityTextError: LiveData<Boolean> = _visibilityTextError

    private val _btnSendSmsEnabling: MutableLiveData<BtnSendSmsEnablingUi> = MutableLiveData()
    val btnSendSmsEnabling: LiveData<BtnSendSmsEnablingUi> = _btnSendSmsEnabling

    private val _textSendSmsTimer: MutableLiveData<TextSendSmsTimerUi> = MutableLiveData()
    val textSendSmsTimer: LiveData<TextSendSmsTimerUi> = _textSendSmsTimer

    private val _hideKeyboard: MutableLiveData<Unit> = MutableLiveData()
    val hideKeyboard: LiveData<Unit> = _hideKeyboard

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

    override fun onValidOtpCodeRequest(otpCode: String) {
        _hideKeyboard.value = Unit
        if (otpCode.length < LENGTH_PIN_CODE) {
            onSmallLengthValidPinCode()
            return
        }
        viewModelScope.launch {
            _visibilityTextError.value = false
            _showLoader.value = true
            delay(1000)
            val request = ValidOtpUseCase.Params(parameters.authyId, otpCode)
            val result = validPinCode(request)
            onRenderValidPinCode(result)
            _showLoader.value = false
        }
    }

    override fun onRenderValidPinCode(result: ResultData<ValidOtpCodeDomain>) {
        when (result) {
            is ResultData.Success -> {
                onSuccessValidPinCode()
            }
            is ResultData.Fail -> onFailValidPinCode(ValidOtp.Fail(result.error))
            is ResultData.NetworkError -> onNetworkError()
        }
    }

    override fun onSuccessValidPinCode() {
        _colorPinView.value = R.color.subtitle_otp
        _visibilityTextError.value = false
        navigate(
            MainScreens.PinCode(
                PinCodeParameters(
                    phoneNumber = parameters.phoneNumber,
                    authyId = parameters.authyId,
                    code = parameters.code,
                    isoCode = parameters.isoCode
                )
            )
        )
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
        _showMsgError.setValue(failValidPinCode.errorDomain.message)
    }

    override fun onSendSms() {
        _textSendSmsTimer.value = TextSendSmsTimerUi(false)
        viewModelScope.launch {
            val request = ResendSmsUseCase.Params(parameters.authyId)
            val result = sendSms(request)
            onRenderSendSms(result)
        }
    }

    override fun onRenderSendSms(result: ResultData<ResendOtpCodeDomain>) {
        when (result) {
            is ResultData.Success -> {
                onSuccessSendSms()
            }
            is ResultData.Fail -> onFailSendSms(SendSms.Fail(result.error))
            is ResultData.NetworkError -> onNetworkError()
        }
        onCounterSendSms()
    }

    override fun onSuccessSendSms() = Unit

    override fun onFailSendSms(failSendSms: SendSms.Fail) {
        _showMsgError.setValue(failSendSms.errorDomain.message)
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
        backTo(MainScreens.Terms)
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            @Assisted("otpParameters") parameters: OtpParameters
        ): OtpViewModel
    }

    companion object {
        private const val LENGTH_PIN_CODE = 6
        private const val MAX_SEND_SMS = 5
        private const val TIME_BAN_SEND_SMS_OF_SECONDS = 10
    }
}