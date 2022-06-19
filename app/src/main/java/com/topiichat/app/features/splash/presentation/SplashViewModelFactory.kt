package com.topiichat.app.features.splash.presentation

import android.app.Activity
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.topiichat.app.features.splash.domain.usecases.FetchTokenUseCase
import javax.inject.Inject

class SplashViewModelFactory @Inject constructor(
    private val fetchTokenUseCase: FetchTokenUseCase,
    owner: Activity
): AbstractSavedStateViewModelFactory(owner as SavedStateRegistryOwner, null) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        @Suppress("UNCHECKED_CAST")
        return SplashViewModel(fetchTokenUseCase) as T
    }
}