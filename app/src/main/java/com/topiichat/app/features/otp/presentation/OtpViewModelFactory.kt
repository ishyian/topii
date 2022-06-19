package com.topiichat.app.features.otp.presentation

import android.app.Activity
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.topiichat.app.features.otp.domain.usecases.SendSmsUseCase
import com.topiichat.app.features.otp.domain.usecases.ValidOtpUseCase
import javax.inject.Inject

class OtpViewModelFactory @Inject constructor(
    private val validPinCode: ValidOtpUseCase,
    private val sendSms: SendSmsUseCase,
    owner: Activity
): AbstractSavedStateViewModelFactory(owner as SavedStateRegistryOwner, null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        @Suppress("UNCHECKED_CAST")
        return OtpViewModel(validPinCode, sendSms) as T
    }
}