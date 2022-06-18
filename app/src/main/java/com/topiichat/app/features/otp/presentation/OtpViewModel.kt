package com.topiichat.app.features.otp.presentation

import android.view.View
import com.topiichat.app.R
import com.topiichat.app.core.navigation.Navigator
import com.topiichat.app.core.platform.BaseViewModel

class OtpViewModel : BaseViewModel(), IOtpViewModel {

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.next_after_otp -> {
                onClickNextAfterOtp()
            }
            R.id.image_view_back -> {
                onClickBack()
            }
            R.id.image_view_close -> {
                onClickClose()
            }
        }
    }

    override fun onClickNextAfterOtp() {
        _navigate.setValue(Navigator(R.id.action_otp_to_pinCode))
    }

    override fun onClickClose() {
        _navigate.setValue(Navigator(R.id.action_otp_to_terms))
    }
}