package com.topiichat.app.features.splash.presentation

import android.view.View
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.navigation.Navigator
import com.topiichat.app.core.platform.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : BaseViewModel(), ISplashViewModel {

    init {
        onLoaderStart()
    }

    override fun onLoaderStart() {
        _showLoader.value = false
        viewModelScope.launch {
            delay(TIME_LOADER)
            _showLoader.value = true
            delay(TIME_LOADER)
            _navigate.setValue(Navigator(R.id.action_splash_to_terms))
        }
    }

    override fun onClick(view: View?) = Unit

    companion object {
        private const val TIME_LOADER = 500L
    }
}