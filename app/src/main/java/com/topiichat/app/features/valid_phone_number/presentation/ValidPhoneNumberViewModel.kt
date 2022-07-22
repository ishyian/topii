package com.topiichat.app.features.valid_phone_number.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.constants.Constants.INITIAL_COUNTRY_ISO_CODE
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.otp.presentation.OtpParameters
import com.topiichat.app.features.valid_phone_number.domain.model.ValidPhone
import com.topiichat.app.features.valid_phone_number.domain.model.VerifyPhoneDomain
import com.topiichat.app.features.valid_phone_number.domain.usecases.VerifyPhoneNumberUseCase
import com.topiichat.app.features.valid_phone_number.presentation.model.PhoneNumber
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

class ValidPhoneNumberViewModel @AssistedInject constructor(
    private val verifyPhoneNumber: VerifyPhoneNumberUseCase,
    appRouter: Router
) : BaseViewModel(appRouter), IValidPhoneNumberViewModel {

    private val _visibilityTextError: MutableLiveData<Boolean> = MutableLiveData()
    val visibilityTextError: LiveData<Boolean> = _visibilityTextError

    private val _hideKeyboard: MutableLiveData<Unit> = MutableLiveData()
    val hideKeyboard: LiveData<Unit> = _hideKeyboard

    private var _isoCode: String = INITIAL_COUNTRY_ISO_CODE
    val isoCode: String = _isoCode

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

    override fun onVerifyPhoneNumberRequest(phoneNumber: PhoneNumber) {
        viewModelScope.launch {
            _hideKeyboard.value = Unit
            _visibilityTextError.value = false
            _showLoader.value = true
            delay(200)
            val request = VerifyPhoneNumberUseCase.Params(
                phoneNumber = phoneNumber.number,
                code = phoneNumber.code,
                isoCode = phoneNumber.isoCode
            )
            val result = verifyPhoneNumber(request)
            onRenderVerifyPhoneNumber(result, phoneNumber.isoCode)
            _showLoader.value = false
        }
    }

    override fun onRenderVerifyPhoneNumber(result: ResultData<VerifyPhoneDomain>, isoCode: String?) {
        when (result) {
            is ResultData.Success -> {
                result.data?.let {
                    _isoCode = isoCode ?: INITIAL_COUNTRY_ISO_CODE
                    onNextAfterValidate(it.phoneNumber, it.authyId, it.code)
                }
            }
            is ResultData.Fail -> onFailValidPhoneNumber(ValidPhone.Fail(result.error))
            is ResultData.NetworkError -> onNetworkError()
        }
    }

    override fun onEmptyPhoneNumber() {
        _showMsgError.setValue("Phone number is empty")
    }

    override fun onNotMatchPinCode() {
        _visibilityTextError.value = true
    }

    override fun onFailValidPhoneNumber(failValidPhone: ValidPhone.Fail) {
        _showMsgError.setValue(failValidPhone.error.message)
    }

    override fun onNextAfterValidate(phoneNumber: String, authyId: String, code: String) {
        navigate(
            MainScreens.Otp(
                OtpParameters(
                    phoneNumber = phoneNumber,
                    authyId = authyId,
                    code = code
                )
            )
        )
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): ValidPhoneNumberViewModel
    }
}