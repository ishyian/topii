package com.topiichat.app.features.valid_phone_number.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.otp.presentation.OtpFragment
import com.topiichat.app.features.valid_phone_number.domain.usecases.ValidPhoneNumberUseCase
import com.topiichat.app.features.valid_phone_number.domain.model.ValidPhone
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ValidPhoneNumberViewModel @Inject constructor(
    private val validPhoneNumber: ValidPhoneNumberUseCase
) : BaseViewModel(), IValidPhoneNumberViewModel {

    private val _visibilityTextError: MutableLiveData<Boolean> = MutableLiveData()
    val visibilityTextError: LiveData<Boolean> = _visibilityTextError

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

    override fun onValidPhoneNumberRequest(phoneNumber: String) {
        viewModelScope.launch {
            _visibilityTextError.value = false
            _showLoader.value = true
            delay(200)
            val request = ValidPhoneNumberUseCase.Params(
                phoneNumber = phoneNumber.trim().replace(" ", "")
            )
            val result = validPhoneNumber(request)
            onRenderValidPhoneNumber(result)
            _showLoader.value = false
        }
    }

    override fun onRenderValidPhoneNumber(validPhone: ValidPhone) {
        when (validPhone) {
            is ValidPhone.Success -> {
                onSuccessValidPhoneNumber(validPhone)
            }
            is ValidPhone.EmptyPhoneNumber -> {
                onEmptyPhoneNumber()
            }
            is ValidPhone.NotMatchPinCode -> {
                onNotMatchPinCode()
            }
            is ValidPhone.Fail -> {
                onFailValidPhoneNumber(validPhone)
            }
        }
    }

    override fun onSuccessValidPhoneNumber(successValidPhone: ValidPhone.Success) {
        onNextAfterValidate(successValidPhone.phoneNumber)
    }

    override fun onEmptyPhoneNumber() {
        _showMsgError.value = "Phone number is empty"
    }

    override fun onNotMatchPinCode() {
        _visibilityTextError.value = true
    }

    override fun onFailValidPhoneNumber(failValidPhone: ValidPhone.Fail) {
        _showMsgError.value = failValidPhone.msgError
    }

    override fun onNextAfterValidate(phoneNumber: String) {
        _navigate.setValue(Navigator(
            actionId = R.id.action_validPhoneNumber_to_otp,
            data = OtpFragment.makeArgs(phoneNumber)
        ))
    }

    override fun onClickClose() {
        _navigate.setValue(Navigator(R.id.action_validPhoneNumber_to_terms))
    }
}