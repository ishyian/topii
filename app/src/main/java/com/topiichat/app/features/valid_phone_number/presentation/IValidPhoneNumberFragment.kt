package com.topiichat.app.features.valid_phone_number.presentation

import com.google.android.material.textfield.TextInputLayout
import com.topiichat.app.core.presentation.platform.IBaseFragment

interface IValidPhoneNumberFragment : IBaseFragment {
    fun onVisibilityTextError(isVisible: Boolean)
    fun onHideKeyboardEvent(ignore: Unit)
    fun setupPhoneNumberInputField(inputLayout: TextInputLayout)
}