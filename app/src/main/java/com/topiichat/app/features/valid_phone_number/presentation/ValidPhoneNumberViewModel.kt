package com.topiichat.app.features.valid_phone_number.presentation

import android.view.View
import com.topiichat.app.R
import com.topiichat.app.core.navigation.Navigator
import com.topiichat.app.core.platform.BaseViewModel

class ValidPhoneNumberViewModel : BaseViewModel(), IValidPhoneNumberViewModel {

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.next_after_validate -> {
                onClickNextAfterValidate()
            }
            R.id.image_view_back -> {
                onClickBack()
            }
            R.id.image_view_close -> {
                onClickClose()
            }
        }
    }

    override fun onClickNextAfterValidate() {
        _navigate.setValue(Navigator(R.id.action_validPhoneNumber_to_otp))
    }

    override fun onClickClose() {
        _navigate.setValue(Navigator(R.id.action_validPhoneNumber_to_terms))
    }
}