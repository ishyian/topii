package com.topiichat.app.features.pin_code.presentation

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.pin_code.domain.ValidPinCode
import com.topiichat.app.features.registration.presentation.RegisterParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.terrakok.cicerone.Router

class PinCodeViewModel @AssistedInject constructor(
    @Assisted("pinCodeParameters") private val parameters: PinCodeParameters,
    appRouter: Router
) : BaseViewModel(appRouter), IPinCodeViewModel {

    private val _showPassTransformationMethod: MutableLiveData<TransformationMethod> =
        MutableLiveData()
    val showPassTransformationMethod: LiveData<TransformationMethod> =
        _showPassTransformationMethod

    private val _pwdCheck: MutableLiveData<Boolean> = MutableLiveData()

    private val _showPassImage: MutableLiveData<Int> = MutableLiveData()
    val showPassImage: LiveData<Int> = _showPassImage

    private val _visibilityTextContentTitle: MutableLiveData<Boolean> = MutableLiveData()
    val visibilityTextContentTitle: LiveData<Boolean> = _visibilityTextContentTitle

    private val _visibilityTextDescription: MutableLiveData<Boolean> = MutableLiveData()
    val visibilityTextDescription: LiveData<Boolean> = _visibilityTextDescription

    private val _visibilityTextError: MutableLiveData<Boolean> = MutableLiveData()
    val visibilityTextError: LiveData<Boolean> = _visibilityTextError

    private val _colorEditTextPinCode: MutableLiveData<Int> = MutableLiveData()
    val colorEditTextPinCode: LiveData<Int> = _colorEditTextPinCode

    private val _textPinCode: MutableLiveData<String> = MutableLiveData()
    val textPinCode: LiveData<String> = _textPinCode

    private var pinCodeCrated = ""

    init {
        _pwdCheck.value = false
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_show_pass -> {
                onClickShowPass()
            }
            R.id.image_view_back -> {
                onClickBack()
            }
            R.id.image_view_close -> {
                onClickClose()
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

    override fun onCheckPinCode(pinCode: String) {
        val validPinCode = if (pinCodeCrated.isEmpty()) {
            onCheckValidPinCode(pinCode)
        } else {
            onConfirmPinCode(pinCode)
        }
        onRenderValidPinCode(validPinCode)
    }

    override fun onCheckValidPinCode(pinCode: String): ValidPinCode {
        return when {
            pinCode.isEmpty() -> {
                ValidPinCode.Empty
            }
            pinCode.length < MIN_COUNT_SYMBOLS_PIN_CODE -> {
                ValidPinCode.ErrorCountSymbols
            }
            else -> {
                ValidPinCode.Success(pinCode)
            }
        }
    }

    override fun onConfirmPinCode(pinCode: String): ValidPinCode {
        return if (pinCode != pinCodeCrated) {
            ValidPinCode.ErrorMatches
        } else {
            ValidPinCode.Success(pinCode)
        }
    }

    override fun onRenderValidPinCode(validPinCode: ValidPinCode) {
        when (validPinCode) {
            is ValidPinCode.Success -> {
                onSuccessValidPinCode(validPinCode.pinCode)
            }
            is ValidPinCode.Empty -> {
                onEmptyValidPinCode()
            }
            is ValidPinCode.ErrorCountSymbols -> {
                onErrorCountSymbolsValidPinCode()
            }
            is ValidPinCode.ErrorConsecutiveNumbers -> {
                onErrorConsecutiveNumbersValidPinCode()
            }
            is ValidPinCode.ErrorSameNumbers -> {
                onErrorSameNumbersValidPinCode()
            }
            is ValidPinCode.ErrorMatches -> {
                onErrorMatches()
            }
        }
    }

    override fun onSuccessValidPinCode(pinCode: String) {
        if (pinCodeCrated.isEmpty()) {
            _visibilityTextContentTitle.value = true
            _visibilityTextDescription.value = false
            _textPinCode.value = ""
            pinCodeCrated = pinCode
        } else {
            pinCodeCrated = ""
            _visibilityTextContentTitle.value = false
            _visibilityTextDescription.value = true
            _visibilityTextError.value = false
            _colorEditTextPinCode.value = R.color.pin_code_text_color
            navigate(
                MainScreens.Register(
                    RegisterParameters(
                        phoneNumber = parameters.phoneNumber,
                        authyId = parameters.authyId,
                        code = parameters.code,
                        pinCode = pinCode
                    )
                )
            )
        }
    }

    override fun onEmptyValidPinCode() {
        _showMsgError.setValue("pin code is empty")
    }

    override fun onErrorCountSymbolsValidPinCode() {
        _showMsgError.setValue("pin code isn't contains $MIN_COUNT_SYMBOLS_PIN_CODE symbols")
    }

    override fun onErrorConsecutiveNumbersValidPinCode() {
        _showMsgError.setValue("pin code is contains $MAX_CONSECUTIVE_NUMBERS consecutive numbers")
    }

    override fun onErrorSameNumbersValidPinCode() {
        _showMsgError.setValue("pin code is contains $MAX_SAME_NUMBERS same numbers")
    }

    override fun onErrorMatches() {
        _visibilityTextError.value = true
        _colorEditTextPinCode.value = R.color.error_color
    }

    override fun onClickClose() {
        backTo(MainScreens.Terms)
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            @Assisted("pinCodeParameters") parameters: PinCodeParameters
        ): PinCodeViewModel
    }

    companion object {
        private const val MIN_COUNT_SYMBOLS_PIN_CODE = 6
        private const val MAX_CONSECUTIVE_NUMBERS = 3
        private const val MAX_SAME_NUMBERS = 3
    }
}