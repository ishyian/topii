package com.topiichat.app.features.splash.presentation

import android.view.View
import androidx.lifecycle.viewModelScope
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getAccessToken: GetAuthDataUseCase,
    appRouter: Router
) : BaseViewModel(appRouter), ISplashViewModel {

    init {
        onLoaderStart()
    }

    override fun onLoaderStart() {
        _showLoader.value = false
        viewModelScope.launch {
            delay(TIME_LOADER)
            _showLoader.value = true
            delay(TIME_LOADER)
            fetchToken()
        }
    }

    override fun onClick(view: View?) = Unit

    private suspend fun fetchToken() {
        val result = getAccessToken()
        if (result.token.isEmpty()) {
            navigate(MainScreens.Terms, true)
        } else navigate(MainScreens.Home, true)
    }

    companion object {
        private const val TIME_LOADER = 2000L
    }
}