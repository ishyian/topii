package com.topiichat.app.features.otp.presentation

import android.view.View
import com.topiichat.app.R
import com.topiichat.app.core.platform.BaseViewModel

class OtpViewModel : BaseViewModel(), IOtpViewModel {

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.next_after_otp -> {
                onClickNextAfterOtp()
            }
        }
    }

    override fun onClickNextAfterOtp() {
        _navigate.value = R.id.action_otp_to_pinCode
    }
}