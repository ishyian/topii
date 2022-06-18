package com.topiichat.app.features.pin_code.presentation

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.core.platform.BaseViewModel

class PinCodeViewModel : BaseViewModel(), IPinCodeViewModel {

    private val _showPassTransformationMethod: MutableLiveData<TransformationMethod> =
        MutableLiveData()
    val showPassTransformationMethod: LiveData<TransformationMethod> =
        _showPassTransformationMethod

    private val _pwdCheck: MutableLiveData<Boolean> = MutableLiveData()

    private val _showPassImage: MutableLiveData<Int> = MutableLiveData()
    val showPassImage: LiveData<Int> = _showPassImage

    init {
        _pwdCheck.value = false
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.show_pass_btn -> {
                onClickShowPass()
            }
            R.id.next_after_pin_code -> {
                onClickNextAfterPinCode()
            }
        }
    }

    override fun onClickShowPass() {
        val pwdCheck = _pwdCheck.value ?: return
        if (pwdCheck.not()) {
            _pwdCheck.value = true
            _showPassTransformationMethod.value = HideReturnsTransformationMethod.getInstance()
            _showPassImage.value = R.drawable.ic_password_yes
        } else {
            _pwdCheck.value = false
            _showPassTransformationMethod.value = PasswordTransformationMethod.getInstance()
            _showPassImage.value = R.drawable.ic_password_not
        }
    }

    override fun onClickNextAfterPinCode() {
        _navigate.value = R.id.action_pinCode_to_home
    }
}