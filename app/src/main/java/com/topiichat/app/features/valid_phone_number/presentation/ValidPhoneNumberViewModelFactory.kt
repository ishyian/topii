package com.topiichat.app.features.valid_phone_number.presentation

import android.app.Activity
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.topiichat.app.features.valid_phone_number.domain.usecases.ValidPhoneNumberUseCase
import javax.inject.Inject

class ValidPhoneNumberViewModelFactory @Inject constructor(
    private val validPhoneNumber: ValidPhoneNumberUseCase,
    owner: Activity
) : AbstractSavedStateViewModelFactory(owner as SavedStateRegistryOwner, null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        @Suppress("UNCHECKED_CAST")
        return ValidPhoneNumberViewModel(validPhoneNumber) as T
    }

}