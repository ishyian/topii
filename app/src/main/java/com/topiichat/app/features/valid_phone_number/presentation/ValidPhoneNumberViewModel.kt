package com.topiichat.app.features.valid_phone_number.presentation

import android.view.View
import com.topiichat.app.R
import com.topiichat.app.core.platform.BaseViewModel

class ValidPhoneNumberViewModel : BaseViewModel(), IValidPhoneNumberViewModel {

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.next_after_validate -> {
                onClickNextAfterValidate()
            }
        }
    }

    override fun onClickNextAfterValidate() {
        _navigate.value = R.id.action_validPhoneNumber_to_otp
    }
}