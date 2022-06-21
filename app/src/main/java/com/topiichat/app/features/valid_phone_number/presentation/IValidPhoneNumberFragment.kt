package com.topiichat.app.features.valid_phone_number.presentation

import com.topiichat.app.core.presentation.platform.IBaseFragment

interface IValidPhoneNumberFragment : IBaseFragment {
    fun onShowMessageError(message: String)
    fun onVisibilityTextError(isVisible: Boolean)
}